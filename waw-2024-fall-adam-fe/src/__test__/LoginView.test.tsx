import { render, screen ,fireEvent} from '@testing-library/react';
import '@testing-library/jest-dom';
import LoginView from '../components/LoginView/LoginView';
import { Provider } from 'react-redux';
import store from '../store/store';
import { MemoryRouter } from 'react-router-dom';

describe('LoginView tests', () => {
    
 
    test('renders LoginView component with expected elements',async () => {
        render(
            <MemoryRouter>
                <Provider store={store}>
                    <LoginView />
                </Provider>
            </MemoryRouter>
        );
        expect(screen.getByText("Sign in with Single Sign-On")).toBeInTheDocument();
        expect(screen.getByText("Your SSO email or domain")).toBeInTheDocument();
        expect(screen.getByText("Sign in")).toBeInTheDocument();
        const input =  (await screen.findByPlaceholderText("name@company.com or company.com"));
        fireEvent.change(input,{target:"name@"});
        const Btn = screen.getByRole("button",{name:"Sign in"});
        fireEvent.click(Btn);
    });
});
