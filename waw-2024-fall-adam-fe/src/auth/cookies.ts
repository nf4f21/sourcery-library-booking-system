import Cookies from 'universal-cookie';
import AuthTokens from '../models/AuthTokens';

const cookies = new Cookies(null, { path: '/' });

export function storeAuthTokensInCookies(authTokens: AuthTokens): void {
  cookies.set('token', authTokens.token);
  cookies.set('refreshToken', authTokens.refreshToken);
}

/**
 * @returns token and refreshToken, or null if there are no tokens stored in cookies
 */
export function readAuthTokensFromCookies(): AuthTokens | null {
  const token = cookies.get('token');
  const refreshToken = cookies.get('refreshToken');
  // not authorised - return null
  if (token === undefined || refreshToken === undefined) {
    return null;
  }
  return { token, refreshToken };
}

export function clearAuthTokensFromCookies(): void {
  cookies.remove('token');
  cookies.remove('refreshToken');
}
