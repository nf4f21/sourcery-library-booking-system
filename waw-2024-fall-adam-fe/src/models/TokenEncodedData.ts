export default interface TokenEncodedData {
  defaultOffice: string;
  firstName: string;
  lastName: string;
  /**
   * sub = email
   */
  sub: string;
  userRoles: string[];
}