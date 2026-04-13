import '@testing-library/jest-dom';
import { clearAuthTokensFromCookies, readAuthTokensFromCookies, storeAuthTokensInCookies } from '../../auth/cookies';
import sampleAuthTokens from './sampleAuthTokens.json';

describe('App tests', () => {
  test('Auth tokens can be stored and then read from cookies', () => {
    // act
    storeAuthTokensInCookies(sampleAuthTokens);
    const retrievedTokens = readAuthTokensFromCookies();
    // assert
    expect(retrievedTokens).not.toBeNull();
    expect(retrievedTokens?.token).toBe(sampleAuthTokens.token);
    expect(retrievedTokens?.refreshToken).toBe(sampleAuthTokens.refreshToken);
  });

  test('Auth tokens are successfully removed from cookies', () => {
    // arrange
    storeAuthTokensInCookies(sampleAuthTokens);
    // act
    clearAuthTokensFromCookies();
    const retrievedTokens = readAuthTokensFromCookies();
    // assert
    expect(retrievedTokens).toBeNull();
  });
});
