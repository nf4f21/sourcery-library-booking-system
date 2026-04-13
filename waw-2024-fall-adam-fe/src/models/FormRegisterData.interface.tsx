import NewUser from "./NewUser.interface";

interface FormRegisterData extends NewUser {
  repeatedPassword: string;
}

export default FormRegisterData;
