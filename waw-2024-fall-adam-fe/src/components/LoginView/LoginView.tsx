import CircularProgress from "@mui/material/CircularProgress";
import { useState } from "react";
import "./LoginView.css";
import axios from "axios";
import * as z from "zod";
import { useForm, SubmitHandler } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import EmailForm from "./EmailForm";
import PasswordForm from "./PasswordForm";
import AuthTokens from "../../models/AuthTokens";
import { storeAuthTokensInCookies } from "../../auth/cookies";
import RegisterLoginBackgrod from "../RegisterView/RegisterLoginBackground";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { login } from "../../store/slices/userSlice";
import { jwtDecode } from "jwt-decode";
import TokenEncodedData from "../../models/TokenEncodedData";

const schema = z.object({
  email: z
    .string()
    .min(1, { message: "Email required" })
    .email({ message: "Incorrect email" }),
  password: z
    .string()
    .min(1, { message: "Password is required" })
    .min(8, { message: "Password must be at least 8 characters long" }),
});

enum ViewType {
  EMAIL_FORM,
  PASSWORD_FORM,
  LOADING_PAGE,
}

const LoginView = () => {
  const [form, setForm] = useState<ViewType>(ViewType.EMAIL_FORM);
  const [buttonClicked, setButtonClicked] = useState<Boolean>(false);
  type FormFields = z.infer<typeof schema>;

  const dispatch = useDispatch();
  const navigate = useNavigate();

  const {
    register: user,
    handleSubmit,
    getValues,
    trigger,
    formState: { errors },
  } = useForm<FormFields>({
    resolver: zodResolver(schema),
    mode: "onBlur",
  });
  const emailButtonClicked = async () => {
    setForm(ViewType.PASSWORD_FORM);
  };
  const passwordButtonClicked = async () => {
    setForm(ViewType.LOADING_PAGE);
    const userData = getValues();
    axios
      .post<AuthTokens>("http://localhost:8080/api/v1/auth/authorize", userData)
      .then((response) => {
        const authTokens = response.data;
        storeAuthTokensInCookies(authTokens);
        const jwtTokenData: TokenEncodedData = jwtDecode(authTokens.token);
        dispatch(login({
          name: jwtTokenData.firstName,
          email: jwtTokenData.sub,
          office: jwtTokenData.defaultOffice,
          isAdmin: jwtTokenData.userRoles.includes('ADMIN'),
        }))
        navigate('/');
      })
      .catch((error) => {
        console.error("Setting account failed", error.response);
      });
  };
  const emailSubmitHandler: SubmitHandler<any> = async (data) => {
    const valid = await trigger("email");
    if (valid) setForm(ViewType.PASSWORD_FORM);
  };
  const passwordSubmitHandler: SubmitHandler<any> = async (data) => {
    const valid = await trigger("password");
    if (valid) {
      passwordButtonClicked();
    } else setButtonClicked(true);
  };

  return (
    <div className="login-view-main-login">
      <RegisterLoginBackgrod />
      <div className="login-view-frame">
        <img src="./../../../Frame.svg" />
      </div>
      {form === ViewType.EMAIL_FORM && (
        <EmailForm
          handler={handleSubmit(emailButtonClicked, emailSubmitHandler)}
          data={user("email")}
          emailError={errors.email}
        ></EmailForm>
      )}
      {form === ViewType.PASSWORD_FORM && (
        <PasswordForm
          handler={handleSubmit(passwordButtonClicked, passwordSubmitHandler)}
          data={user("password")}
          signedClicked={buttonClicked}
          passwordError={errors.password}
        ></PasswordForm>
      )}
      {form === ViewType.LOADING_PAGE && (
        <div className="login-view-loading">
          <p>Setting your account...</p>
          <CircularProgress className="login-view-progress" />
        </div>
      )}
    </div>
  );
};

export default LoginView;
