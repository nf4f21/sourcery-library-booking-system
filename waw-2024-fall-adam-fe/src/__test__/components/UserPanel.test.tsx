import { render, RenderResult, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import UserPanel from '../../components/UserPanel/UserPanel';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router-dom';
import createTestStore from '../helpers/testStore';
import { UserState } from '../../store/slices/userSlice';

describe('UserPanel tests', () => {
  async function renderUserPanel(preloadedUserState: Partial<UserState> = {}): Promise<RenderResult> {
    const testStore = createTestStore({
      user: preloadedUserState
    })
    const renderResult = render(
      <MemoryRouter>
        <Provider store={testStore}>
          <UserPanel />
        </Provider>
      </MemoryRouter>
    );
    return renderResult;
  }

  test('Log out button should render in the UserPanel when user logged in', async () => {
    // arrange & act
    await renderUserPanel({ isAuthenticated: true });
    const logoutButtonElement = screen.getByText(
      (content, element) => 
        content === 'Log out' && element?.tagName.toLowerCase() === 'span'
    )
    // assert
    expect(logoutButtonElement).toBeInTheDocument();
  });

  test('Log in button should render in the UserPanel when user not logged in', async () => {
    // arrange & act
    await renderUserPanel({ isAuthenticated: false });
    const logoutButtonElement = screen.getByText(
      (content, element) => 
        content === 'Log in' && element?.tagName.toLowerCase() === 'span'
    )
    // assert
    expect(logoutButtonElement).toBeInTheDocument();
  });

  test('Register button should render in the UserPanel when user not logged in', async () => {
    // arrange & act
    await renderUserPanel({ isAuthenticated: false });
    const logoutButtonElement = screen.getByText(
      (content, element) => 
        content === 'Register' && element?.tagName.toLowerCase() === 'span'
    )
    // assert
    expect(logoutButtonElement).toBeInTheDocument();
  });

  test('User panel displays actual user data', async () => {
    const sampleUserData: UserState = {
      name: 'Cole',
      email: 'colepalmer@example.com',
      office: 'Dublin',
      isAuthenticated: true,
      isAdmin: false,
    };
    await renderUserPanel(sampleUserData);
    expect(screen.getByText(`Hello, ${sampleUserData.name}!`)).toBeInTheDocument();
    expect(screen.getByText(sampleUserData.email)).toBeInTheDocument();
    expect(screen.getByText(`${sampleUserData.office} Office`)).toBeInTheDocument();
  });
});
