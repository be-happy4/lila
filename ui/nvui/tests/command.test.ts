import { expect, test } from 'vitest';
import { commands } from '../src/command';
import { Pieces } from 'chessground/types';

const pieces: Pieces = new Map();
pieces.set('a1', {
  role: 'king',
  color: 'white',
});
pieces.set('a2', {
  role: 'queen',
  color: 'white',
});
pieces.set('b1', {
  role: 'knight',
  color: 'white',
});
pieces.set('b2', {
  role: 'knight',
  color: 'white',
});

test('piece command', () => {
  expect(commands.piece.apply('p N', pieces, 'san')).toBe('white knight: b1, b2');
  expect(commands.piece.apply('p N', pieces, 'nato')).toBe('white knight: bravo 1, bravo 2');
  expect(commands.piece.apply('p b', pieces, 'san')).toBe('black bishop: none');

  expect(commands.piece.apply('p X', pieces, 'san')).toBeUndefined();
  expect(commands.piece.apply('p |', pieces, 'san')).toBeUndefined();
});

test('scan command', () => {
  expect(commands.scan.apply('s a', pieces, 'san')).toBe('a1 white king, a2 white queen');
  expect(commands.scan.apply('s 1', pieces, 'san')).toBe('a1 white king, b1 white knight');

  expect(commands.scan.apply('s x', pieces, 'san')).toBeUndefined();
  expect(commands.scan.apply('s 9', pieces, 'san')).toBeUndefined();
});