import { Link, useLocation } from "react-router-dom";
import { Button } from "./retroui/Button";
export default function Navbar() {
  const location = useLocation();
  const pathname = location.pathname;

  const isActive = (path) => pathname === path;

  return (
    <nav className="border-b border-border bg-card/50 backdrop-blur-sm sticky top-0 z-50">
      <div className="max-w-6xl mx-auto px-4 h-16 flex items-center justify-between">
        {/* Logo */}
        <Link
          to="/"
          className="flex items-center gap-2 font-bold text-lg text-foreground hover:text-primary transition-colors"
        >
          <div className="w-8 h-8 rounded bg-primary flex items-center justify-center text-primary-foreground text-sm font-bold">
            DV
          </div>
          DataVeil
        </Link>

        {/* Navigation Links */}
        <div className="hidden md:flex items-center gap-8">
          <Link
            to="/"
            className={`text-sm transition-colors ${
              isActive("/")
                ? "text-primary font-semibold"
                : "text-muted-foreground hover:text-foreground"
            }`}
          >
            Home
          </Link>

          <Link
            to="/submit"
            className={`text-sm transition-colors ${
              isActive("/submit")
                ? "text-primary font-semibold"
                : "text-muted-foreground hover:text-foreground"
            }`}
          >
            Submit
          </Link>

          <Link
            to="/dashboard"
            className={`text-sm transition-colors ${
              isActive("/dashboard")
                ? "text-primary font-semibold"
                : "text-muted-foreground hover:text-foreground"
            }`}
          >
            Dashboard
          </Link>

          <Link
            to="/logs"
            className={`text-sm transition-colors ${
              isActive("/logs")
                ? "text-primary font-semibold"
                : "text-muted-foreground hover:text-foreground"
            }`}
          >
            Logs
          </Link>

          {/* <Link
            to="/profile"
            className={`text-sm transition-colors ${
              isActive("/profile")
                ? "text-primary font-semibold"
                : "text-muted-foreground hover:text-foreground"
            }`}
          >
            Profile
          </Link>

          <Link
            to="/settings"
            className={`text-sm transition-colors ${
              isActive("/settings")
                ? "text-primary font-semibold"
                : "text-muted-foreground hover:text-foreground"
            }`}
          >
            Settings
          </Link> */}
        </div>

        {/* Mobile Menu */}
        <div className="md:hidden flex gap-2">
          <Button variant="ghost" size="sm" asChild>
            <Link to="/submit">Submit</Link>
          </Button>
          <Button size="sm" asChild>
            <Link to="/dashboard">Dashboard</Link>
          </Button>
        </div>
      </div>
    </nav>
  );
}
