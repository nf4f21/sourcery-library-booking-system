import TextField from "@mui/material/TextField";
import React, { useState } from "react";
import "./LoginView.css";
import {
  SubmitHandler,
  FieldError,
  UseFormRegisterReturn,
} from "react-hook-form";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";

type Props = {
  handler: SubmitHandler<any>;
  data: UseFormRegisterReturn<"password">;
  signedClicked: Boolean;
  passwordError: undefined | FieldError;
};
const PasswordForm: React.FC<Props> = ({
  handler,
  data,
  signedClicked,
  passwordError,
}) => {
  const [passwordVisibility, setPasswordVisibility] = useState<boolean>(false);

  const handlePasswordVisible = () => {
    setPasswordVisibility((prevState) => !prevState);
  };

  return (
    <form className="login-view-form-main" onSubmit={handler}>
      <div className="login-view-main-text">Type in your password</div>
      <div className="login-view-small-text">
        Use your work email account login details
      </div>
      <TextField
        {...data}
        id="outlined-basic"
        type={passwordVisibility ? "text" : "password"}
        variant="outlined"
        className="login-view-input"
        size="small"
      />
      <div className="login-view-icon-password-login" onClick={handlePasswordVisible}>
        {!passwordVisibility ? (
          <VisibilityOffIcon
            sx={{ fontSize: "16px", color: "#050237", opacity: 0.7 }}
          />
        ) : (
          <VisibilityIcon
            sx={{ fontSize: "16px", color: "#050237", opacity: 0.7 }}
          />
        )}
      </div>
      {signedClicked && passwordError && (
        <div className="login-view-errors">{passwordError.message}</div>
      )}
      <button className="login-view-sign-in" type="submit">
        Sign in
      </button>
    </form>
  );
};
export default PasswordForm;
