import HelpOutlineIcon from '@mui/icons-material/HelpOutline';
import NotificationsNoneIcon from '@mui/icons-material/NotificationsNone';
import SearchIcon from '@mui/icons-material/Search';
import './Nav.css';

function Nav(): JSX.Element {
    return (
        <nav className="nav-navigation">
          <div className="nav-search-container">
                <input type="text" placeholder="Search books" className="nav-search-input" />
                <span className="nav-search-icon"><SearchIcon/> </span>
            </div>
            <div className="nav-icons-container">
                <div className="nav-help-icon"><HelpOutlineIcon/></div> 
                <div className="nav-notifications-icon"><NotificationsNoneIcon/></div> 
            </div>
        </nav>
    );
};

export default Nav;