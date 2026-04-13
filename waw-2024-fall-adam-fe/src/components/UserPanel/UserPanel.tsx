import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './UserPanel.css';
import DashboardIcon from '@mui/icons-material/Dashboard';
import BookIcon from '@mui/icons-material/Book';
import FavoriteIcon from '@mui/icons-material/Favorite';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import LogoutIcon from '@mui/icons-material/Logout';
import LoginIcon from '@mui/icons-material/Login';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import { useSelector, useDispatch } from 'react-redux';
import { RootState } from '../../store/store';
import { logout } from '../../store/slices/userSlice';import LibraryAddIcon from '@mui/icons-material/LibraryAdd';
import { clearAuthTokensFromCookies, readAuthTokensFromCookies } from '../../auth/cookies';



const UserPanel: React.FC = () => {
	const userEmail = 'nathan.roberts@example.com';
	const userOffice = 'Kaunas Office';
	

	const user = useSelector((state: RootState) => state.user); // Access user data from Redux store
	const dispatch = useDispatch();
	const navigate = useNavigate();

	const handleLogout = () => {
		clearAuthTokensFromCookies(); // Clear cookies
		dispatch(logout()); // Dispatch logout action
		navigate('/login');
	};



	  

	return (
		<div className='user-panel'>
			<div className='user-panel-content-wrapper'>
				<section>
					{/* User Info Section */}
					<div className='user-panel-info'>
						{/* Logo */}
						<div className="user-panel-logo">
        					<img src="./../../../Frame.svg" alt="logo" />
      					</div>
						 
						{/* Placeholder for the image */}
						<div className='user-panel-image'>{/* Empty for now as the image is not being implemented */}</div>

						{/* User Greeting */}
						<h3 className='user-panel-greeting'>Hello, {user.name}!</h3>
						<p className='user-panel-email'>{user.email}</p>
						<p className='user-panel-office'>{user.office} Office</p>
					</div>

					{/* Navigation Links */}
					<nav className='user-panel-nav'>
						{/* Clickable Dashboard Link */}
						<Link to='/' className='user-panel-link'>
							<div
								className='user-panel-nav-item user-panel-nav-item-active'
								onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = '#e0f2ff')}
								onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = 'transparent')}
							>
								<DashboardIcon className='user-panel-icon' />
								<span className='user-panel-nav-text-active'>Dashboard</span>
							</div>
						</Link>

						{/* Non-clickable Library */}
						<div className='user-panel-nav-item user-panel-nav-item-disabled'>
							<BookIcon className='user-panel-icon' />
							<span className='user-panel-nav-text'>Library</span>
						</div>

						{/* Non-clickable Wishlist */}
						<div className='user-panel-nav-item user-panel-nav-item-disabled'>
							<FavoriteIcon className='user-panel-icon' />
							<span className='user-panel-nav-text'>Wishlist</span>
						</div>

						{/* Non-clickable My Reservations */}
						<Link to='/reservations' className='user-panel-link'>
							<div className='user-panel-nav-item '>
								<AccessTimeIcon className='user-panel-icon' />
								<span className='user-panel-nav-text'>My reservations</span>
							</div>
						</Link>

						{/* Add Book - Only Visible to Admin */}
						{user.isAdmin && (
							<Link to='/books/create' className='user-panel-link'>
								<div className='user-panel-nav-item'>
									<LibraryAddIcon className='user-panel-icon' />
									<span className='user-panel-nav-text'>Add Book</span>
								</div>
							</Link>
						)}
					</nav>
				</section>
				<section>
					<nav className='user-panel-nav'>
						{/* Logout button */}
						{user.isAuthenticated && <div className='user-panel-nav-item' onClick={handleLogout}>
							<LogoutIcon className='user-panel-icon' />
							<span className='user-panel-nav-text'>Log out</span>
						</div>}
						{/* Login button */}
						{!user.isAuthenticated && <Link to='/login' className='user-panel-link'>
							<div className='user-panel-nav-item'>
								<LoginIcon className='user-panel-icon' />
								<span className='user-panel-nav-text'>Log in</span>
							</div>
						</Link>}
						{/* Register button */}
						{!user.isAuthenticated && <Link to='/register' className='user-panel-link'>
							<div className='user-panel-nav-item'>
								<PersonAddIcon className='user-panel-icon' />
								<span className='user-panel-nav-text'>Register</span>
							</div>
						</Link>}
					</nav>
				</section>
			</div>
		</div>
	);
};

export default UserPanel;
