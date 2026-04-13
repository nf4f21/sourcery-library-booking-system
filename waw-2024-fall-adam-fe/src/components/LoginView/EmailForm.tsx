import TextField from "@mui/material/TextField";
import React from "react";
import "./LoginView.css";
import {
  SubmitHandler,
  FieldError,
  UseFormRegisterReturn,
} from "react-hook-form";
import { Link } from "react-router-dom";

type Props = {
  handler: SubmitHandler<any>;
  data: UseFormRegisterReturn<"email">;
  emailError: undefined | FieldError;
};
const EmailForm: React.FC<Props> = ({ handler, data, emailError }) => {
  return (
    <form className="login-view-form-main" onSubmit={handler}>
      <div className="login-view-main-text">Sign in with Single Sign-On</div>
      <div className="login-view-small-text">Your SSO email or domain</div>
      <TextField
        {...data}
        className="login-view-input"
        placeholder="name@company.com or company.com"
        size="small"
        sx={{
          "& .MuiInputBase-input::placeholder": {
            fontSize: "clamp(0.85rem, 1vw, 1rem)",
          },
        }}
      />
      {emailError && <div className="login-view-errors">{emailError.message}</div>}
      <button className="login-view-sign-in" type="submit">
        Sign in
      </button>
      <div className="register-login-switcher-wrapper">
        <Link to="/register">Not registered yet?</Link>
      </div>
    </form>
  );
};
export default EmailForm;
