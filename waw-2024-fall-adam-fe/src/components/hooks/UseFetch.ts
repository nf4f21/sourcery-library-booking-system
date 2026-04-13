import { useState, useCallback } from 'react';
import axios, { AxiosRequestConfig } from 'axios';
import { clearAuthTokensFromCookies, readAuthTokensFromCookies, storeAuthTokensInCookies } from '../../auth/cookies';
import { useNavigate } from 'react-router-dom';

const BASE_URL = 'http://localhost:8080/api/v1';

const useFetch = <T = any, U = any>(path: string, options: AxiosRequestConfig = {}) => {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const fetchData = useCallback(async (body?: U) => {
    const url = `${BASE_URL}${path}`;
    setLoading(true);
    setError(null);

    const authTokens = readAuthTokensFromCookies();
    if (authTokens === null) {
      navigate('/login');
    }

    try {
      const token = authTokens!.token; 

      // Make the API request with the token in the headers
      const response = await axios.request({
        ...options,
        url,
        data: body,
        headers: {
          ...options.headers,
          'Authorization': `Bearer ${token}`,
        },
      });

      // Save response data to the state
      setData(response.data);
    } catch (err: any) {
      if (err.response && (err.response.status === 401 || err.response.status === 403)) {
        // Attempt to refresh the token if 401 or 403 error is caught
        try {
          const refreshToken = authTokens!.refreshToken;
          const refreshResponse = await axios.post(`${BASE_URL}/auth/refresh`, {
            token: refreshToken,
          });
          
          storeAuthTokensInCookies({ 
            token: refreshResponse.data.token, 
            refreshToken
          })

          // Retry the original request with the new token
          const retryResponse = await axios.request({
            ...options,
            url,
              data: body,
            headers: {
              ...options.headers,
              'Authorization': `Bearer ${refreshResponse.data.token}`,
            },
          });
          // Save retried response data to the state
          setData(retryResponse.data);
        } catch (refreshError) {
          // If refreshing token fails, logout the user
          setError('Failed to refresh auth token');
          clearAuthTokensFromCookies();
          navigate('/login');
        }
      } else {
        // If other errors occur, set the error message
        setError(err.message || 'An error occurred');
      }
    } finally {
      // Stop loading once the request is complete
      setLoading(false);
    }
  }, [path, options, navigate]);

  // Return the data, loading state, error message, and fetchData function for use in the component
  return { data, loading, error, fetchData };
};

export default useFetch;
