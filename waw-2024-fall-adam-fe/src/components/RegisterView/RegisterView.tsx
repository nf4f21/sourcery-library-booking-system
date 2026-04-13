import { useState, useEffect } from "react";
import FormRegisterData from "../../models/FormRegisterData.interface";
import axios from "axios";
import "./RegisterView.css";
import { useForm, SubmitHandler } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import RegisterLoginBackground from "./RegisterLoginBackground";
import * as z from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { TextField } from "@mui/material";
import "./RegisterView.css";
import { FormControl, InputLabel, MenuItem, Select } from "@mui/material";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import { Link } from "react-router-dom";

const registerSchema = z
  .object({
    firstName: z
      .string()
      .min(1, { message: "First name is required" })
      .regex(/^[A-Z][a-z]*$/, {
        message:
          "First name must start with a capital letter and contains only letters",
      }),
    lastName: z
      .string()
      .min(1, { message: "Last name is required" })
      .regex(/^[A-Z][a-z]*$/, {
        message:
          "Last name must start with a capital letter and contains only letters",
      }),
    email: z
      .string()
      .min(1, { message: "Email is required" })
      .regex(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, {
        message: "Please enter a valid email address",
      }),
    phoneNumber: z
      .string()
      .min(1, { message: "Phone number is required" })
      .regex(/^\+[0-9]{3}[0-9]{3}[0-9]{4,6}$/, {
        message: "Please enter a valid phone number ex. +48147258369",
      }),
    defaultOfficeName: z
      .string()
      .min(1, { message: "Office is required" })
      .refine((val) => val !== "default", {
        message: "Please select a valid office",
      }),
    password: z
      .string()
      .min(1, { message: "Password is required" })
      .regex(/^(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/, {
        message:
          "Must be at least 8 char., contains one uppercase letter, one number and one special char.",
      }),
    repeatedPassword: z
      .string()
      .min(1, { message: "Repeat password is required" }),
  })
  .refine((data) => data.password === data.repeatedPassword, {
    message: "Passwords do not match",
    path: ["repeatedPassword"],
  });

type RegisterFormFields = z.infer<typeof registerSchema>;
type FormData = FormRegisterData & RegisterFormFields;

type Office = {
  officeId: number;
  name: string;
};

const RegisterView = () => {
  const navigate = useNavigate();
  const {
    register: data,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<FormData>({
    resolver: zodResolver(registerSchema),
    mode: "onChange",
  });

  const [officeList, setOfficeList] = useState<Office[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [errorUserMessage, setErrorUserMessage] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const [passwordVisibility, setPasswordVisibility] = useState<boolean>(false);
  const [passwordRepeatedVisibility, setPasswordRepeatedVisibility] =
    useState<boolean>(false);

  const handlePasswordVisible = () => {
    setPasswordVisibility((prevState) => !prevState);
  };

  const handlePasswordRepeatedVisible = () => {
    setPasswordRepeatedVisibility((prevState) => !prevState);
  };

  const fetchOfficces = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/v1/offices");
      if (!response.ok) {
        throw new Error("Problems with get offices");
      }
      const data = await response.json();
      setOfficeList(data);
    } catch (error: unknown) {
      if (error instanceof Error) {
        setError(error.message);
      } else {
        setError("Problem with fetch");
      }
    }
  };

  useEffect(() => {
    fetchOfficces();
  }, []);

  const onRegister: SubmitHandler<FormData> = (data) => {
    axios
      .post("http://localhost:8080/api/v1/auth/register", data)
      .then((response) => {
        if (response.data) {
          setSuccessMessage("User registered");
        }
        setErrorUserMessage(null);
        reset({
          firstName: "",
          lastName: "",
          email: "",
          phoneNumber: "",
          password: "",
          repeatedPassword: "",
          defaultOfficeName: "",
        });

        setTimeout(() => {
          navigate("/login");
        }, 2000);
      })
      .catch((error) => {
        setErrorUserMessage(error.response.data.info);
      });
  };

  return (
    <div className="register-view-sign-up">
      <RegisterLoginBackground />
      <div className="register-view-frame">
        <img src="./../../../Frame.svg" alt="logo" />
      </div>
      <div className="register-view-sign-up-form">
        <div className="register-view-sign-up-main-text">Register New User</div>
        {successMessage && (
          <div className="register-view-registered-message">{successMessage}</div>
        )}
        {errorUserMessage && <div className="register-view-errors">{errorUserMessage}</div>}
        <form onSubmit={handleSubmit(onRegister)}>
          <div className="register-view-input-register-items">
            <div className="register-view-input-register-item">
              <div className="register-view-input-label-text">First name</div>
              <TextField
                {...data("firstName")}
                className="reguster-view-input-register"
                placeholder="First name"
                size="small"
                fullWidth
              />
              {errors.firstName && (
                <div className="register-view-errors">{errors.firstName.message}</div>
              )}
            </div>
            <div className="register-view-input-register-item">
              <div className="register-view-input-label-text">Last name</div>
              <TextField
                {...data("lastName")}
                className="register-view-input-register"
                placeholder="Last name"
                size="small"
                fullWidth
              />
              {errors.lastName && (
                <div className="register-view-errors">{errors.lastName.message}</div>
              )}
            </div>
            <div className="register-view-input-register-item">
              <div className="register-view-input-label-text">Email</div>
              <TextField
                {...data("email")}
                className="register-view-input-register"
                placeholder="name@company.com"
                size="small"
                fullWidth
              />
              {errors.email && (
                <div className="register-view-errors">{errors.email.message}</div>
              )}
            </div>
            <div className="register-view-input-register-item">
              <div className="register-view-input-label-text">Phone number</div>
              <TextField
                {...data("phoneNumber")}
                className="register-view-input-register"
                placeholder="+XXXXXXXXXXX"
                type="tel"
                size="small"
                fullWidth
              />
              {errors.phoneNumber && (
                <div className="register-view-errors">{errors.phoneNumber.message}</div>
              )}
            </div>
            <div className="register-view-input-register-item">
              <div className="register-view-input-label-text">Password</div>
              <TextField
                {...data("password")}
                className="register-view-input-register"
                placeholder="Password"
                type={passwordVisibility ? "text" : "password"}
                size="small"
                fullWidth
              />
              <div className="register-view-icon-password" onClick={handlePasswordVisible}>
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
              {errors.password && (
                <div className="register-view-errors">{errors.password.message}</div>
              )}
            </div>
            <div className="register-view-input-register-item">
              <div className="register-view-input-label-text">Repeat password</div>
              <TextField
                {...data("repeatedPassword")}
                className="register-view-input-register"
                placeholder="Repeat password"
                type={passwordRepeatedVisibility ? "text" : "password"}
                size="small"
                fullWidth
              />
              <div
                className="register-view-icon-password-repeated"
                onClick={handlePasswordRepeatedVisible}
              >
                {!passwordRepeatedVisibility ? (
                  <VisibilityOffIcon
                    sx={{ fontSize: "16px", color: "#050237", opacity: 0.7 }}
                  />
                ) : (
                  <VisibilityIcon
                    sx={{ fontSize: "16px", color: "#050237", opacity: 0.7 }}
                  />
                )}
              </div>
              {errors.repeatedPassword && (
                <div className="register-view-errors">{errors.repeatedPassword.message}</div>
              )}
            </div>
            <div className="register-view-input-register-item">
              <div className="register-view-input-label-text">Office</div>
              <FormControl
                fullWidth
                className="register-view-input-register"
                size="small"
                sx={{ marginBottom: "1rem" }}
              >
                <Select
                  {...data("defaultOfficeName")}
                  displayEmpty
                  data-testid="office-select"
                  defaultValue=""
                  renderValue={(selected) =>
                    !selected ? (
                      <span style={{ color: "#aaa" }}>Select an office</span>
                    ) : (
                      selected
                    )
                  }
                >
                  {officeList.length > 0 ? (
                    officeList.map((office) => (
                      <MenuItem key={office.officeId} value={office.name}>
                        {office.name}
                      </MenuItem>
                    ))
                  ) : (
                    <MenuItem value="" disabled>
                      No offices available
                    </MenuItem>
                  )}
                </Select>
                {errors.defaultOfficeName && (
                  <div className="register-view-errors">
                    {errors.defaultOfficeName.message}
                  </div>
                )}
              </FormControl>
            </div>
          </div>

          <button className="register-view-sign-up-button" type="submit">
            Sign up
          </button>
          <div className="register-login-switcher-wrapper">
            <Link to="/login">Already have an account?</Link>
          </div>
        </form>
      </div>
    </div>
  );
};

export default RegisterView;
