import { createBrowserRouter } from "react-router-dom";
import RootLayout from "../layouts/RootLayout";
import Landing from "../pages/Landing";
import Dashboard from "../pages/Dashboard";
import Submit from "../pages/Submit";
import Guardrail from "../pages/GuardRail";
import Logs from "../pages/Logs";

const router = createBrowserRouter([
  {
    path: "/",
    element: <RootLayout />,
    children: [
      {
        index: true,
        element: <Landing />,
      },
      {
        path: "dashboard",
        element: <Dashboard />,
      },
      {
        path: "submit",
        element: <Submit />,
      },
      {
        path: "guardrail/:id",
        element: <Guardrail />,
      },
      {
        path: "logs",
        element: <Logs />,
      },
    ],
  },
]);

export default router;
