import { createSlice } from '@reduxjs/toolkit';
import { readAuthTokensFromCookies } from '../../auth/cookies';
import { jwtDecode } from 'jwt-decode';
import TokenEncodedData from '../../models/TokenEncodedData';

export interface UserState {
  name: string;
  email: string;
  office: string;
  isAuthenticated: boolean;
  isAdmin: boolean;
}

let initialState: UserState;

const token = readAuthTokensFromCookies()?.token;
if (token === undefined) {
  initialState = {
    name: '',
    email: '',
    office: '',
    isAuthenticated: false,
    isAdmin: false,
  };
} else {
  const jwtTokenData: TokenEncodedData = jwtDecode(token);
  initialState = {
    name: jwtTokenData.firstName,
    email: jwtTokenData.sub,
    office: jwtTokenData.defaultOffice,
    isAuthenticated: true,
    isAdmin: jwtTokenData.userRoles.includes('ADMIN'),
  };
}

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    login: (state, action) => {
      state.name = action.payload.name;
      state.email = action.payload.email;
      state.office = action.payload.office;
      state.isAuthenticated = true;
      state.isAdmin = action.payload.isAdmin;
    },
    logout: (state) => {
      state.name = '';
      state.email = '';
      state.office = '';
      state.isAuthenticated = false;
      state.isAdmin = false;
    },
  },
});

export const { login, logout } = userSlice.actions;
export default userSlice.reducer;
