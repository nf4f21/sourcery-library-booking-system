import { act, render, screen, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import App from '../App';
import { clearAuthTokensFromCookies, storeAuthTokensInCookies } from '../auth/cookies';
import { useNavigate } from 'react-router-dom';

// unfortunately nothing else worked than this code:
jest.mock('@mui/x-date-pickers/internals/demo', () => ({
    DemoContainer: () => null,
    DemoItem: () => null,
}));
jest.mock('axios');

const mockedNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: () => mockedNavigate,
}));

describe('App tests', () => {

    beforeEach(() => {
        // this is needed to avoid redirects, because useFetch first checks for tokens, and then makes a request
        storeAuthTokensInCookies({
            token: '',
            refreshToken: '',
        });
        jest.clearAllMocks();
    })

    test('renders BookListPanel component with expected text', async () => {
        await waitFor(() => render(
            <App />
        ));
        expect(screen.getByText("List books")).toBeInTheDocument();
    });

    test('navigates to login when no tokens in cookies', async () => {
        clearAuthTokensFromCookies();
        await waitFor(() => render(
            <App />
        ));
        expect(mockedNavigate).toHaveBeenCalledWith('/login');
    });
});
