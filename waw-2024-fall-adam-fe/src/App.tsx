import "./App.css";
import MainView from "./components/MainView/MainView";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { Provider } from "react-redux";
import store from "./store/store";
import RegisterView from "./components/RegisterView/RegisterView";
import LoginView from "./components/LoginView/LoginView";

function App(): JSX.Element {
  return (
    <Provider store={store}>
      <Router>
        <Routes>
          <Route path="*" element={<MainView />} />
          <Route path="/register" element={<RegisterView />} />
          <Route path="/login" element={<LoginView />} />
        </Routes>
      </Router>
    </Provider>
  );
}

export default App;
