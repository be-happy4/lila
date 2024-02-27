package lila.relay

import chess.format.pgn.{ Tag, Tags }
import chess.{ FideId, ByColor }

import lila.db.dsl.{ *, given }
import lila.fide.{ FideTC, FidePlayer, FidePlayerApi }

final private class RelayFidePlayerApi(playerApi: FidePlayerApi)(using Executor):

  def enrichGames(tour: RelayTour)(games: RelayGames): Fu[RelayGames] =
    val tc = guessTimeControl(tour) | FideTC.Standard
    games.traverse: game =>
      (game.tags.fideIds zip game.tags.names zip game.tags.titles)
        .traverse:
          case ((fideId, name), title) => playerApi.guessPlayer(fideId, name, UserTitle from title)
        .map: guesses =>
          game.copy(tags = update(game.tags, tc, guesses))

  private def guessTimeControl(tour: RelayTour): Option[FideTC] =
    tour.description.split('|').lift(2).map(_.trim.toLowerCase.replace("classical", "standard")) so: tcStr =>
      FideTC.values.find(tc => tcStr.contains(tc.toString.toLowerCase))

  private def update(tags: Tags, tc: FideTC, fidePlayers: ByColor[Option[FidePlayer]]): Tags =
    chess.Color.all.foldLeft(tags): (tags, color) =>
      tags ++ Tags:
        fidePlayers(color).so: fide =>
          List(
            Tag(_.fideIds(color), fide.id.toString).some,
            Tag(_.names(color), fide.name).some,
            fide.title.map { title => Tag(_.titles(color), title.value) },
            fide.ratingOf(tc).map { rating => Tag(_.elos(color), rating.toString) }
          ).flatten
