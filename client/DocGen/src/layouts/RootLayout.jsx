import { Outlet } from "react-router-dom";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";

export default function RootLayout() {
  return (
    <>
      <Navbar />
      <main className="min-h-[80vh] flex items-center justify-center w-full">
        <Outlet />
      </main>
      <Footer />
    </>
  );
}
