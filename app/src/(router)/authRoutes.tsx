import { RouteObject } from "react-router-dom";
import SignPage from "../pages/sign/sign.page";

const authRoutes: RouteObject[] = [
  {
    path: "/",
    element: <SignPage />,
  },
];
export default authRoutes;
