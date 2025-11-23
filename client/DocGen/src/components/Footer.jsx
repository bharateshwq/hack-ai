import { Github, Twitter, Linkedin } from "lucide-react";

export default function Footer() {
  return (
    <footer className="border-t border-border bg-card/40 backdrop-blur-sm">
      <div className="max-w-7xl mx-auto px-6 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-10">
          {/* Brand Section */}
          <div>
            <div className="flex items-center gap-2 font-bold text-lg text-foreground">
              <div className="w-8 h-8 rounded bg-primary flex items-center justify-center text-primary-foreground text-sm font-bold">
                CD
              </div>
              DataVeil
            </div>
            <p className="mt-3 text-sm text-muted-foreground max-w-xs">
              AI-powered documentation generation for your GitHub projects.
              Fast, accurate, and developer-friendly.
            </p>

            {/* Static Social Icons */}
            <div className="flex gap-4 mt-4">
              <div className="text-muted-foreground hover:text-primary transition-colors cursor-default">
                <Github size={20} />
              </div>
              <div className="text-muted-foreground hover:text-primary transition-colors cursor-default">
                <Twitter size={20} />
              </div>
              <div className="text-muted-foreground hover:text-primary transition-colors cursor-default">
                <Linkedin size={20} />
              </div>
            </div>
          </div>

          {/* Product */}
          <div>
            <h4 className="font-semibold text-foreground mb-4">Product</h4>
            <ul className="space-y-2 text-sm">
              <li className="text-muted-foreground cursor-default hover:text-primary transition-colors">
                Submit Project
              </li>
              <li className="text-muted-foreground cursor-default hover:text-primary transition-colors">
                Dashboard
              </li>
              <li className="text-muted-foreground cursor-default hover:text-primary transition-colors">
                Documentation
              </li>
            </ul>
          </div>

          {/* Company */}
          <div>
            <h4 className="font-semibold text-foreground mb-4">Company</h4>
            <ul className="space-y-2 text-sm">
              <li className="text-muted-foreground cursor-default hover:text-primary transition-colors">
                Home
              </li>
              <li className="text-muted-foreground cursor-default hover:text-primary transition-colors">
                About Us
              </li>
              <li className="text-muted-foreground cursor-default hover:text-primary transition-colors">
                Careers
              </li>
            </ul>
          </div>

          {/* Resources */}
          <div>
            <h4 className="font-semibold text-foreground mb-4">Resources</h4>
            <ul className="space-y-2 text-sm">
              <li className="text-muted-foreground cursor-default hover:text-primary transition-colors">
                Changelog
              </li>
              <li className="text-muted-foreground cursor-default hover:text-primary transition-colors">
                Support
              </li>
              <li className="text-muted-foreground cursor-default hover:text-primary transition-colors">
                Privacy Policy
              </li>
            </ul>
          </div>
        </div>

        {/* Bottom strip */}
        <div className="border-t border-border mt-10 pt-6 flex flex-col md:flex-row justify-between items-center text-xs text-muted-foreground">
          <p>© {new Date().getFullYear()} DataVeil. All rights reserved.</p>
          <p className="mt-2 md:mt-0">
            Built with ❤️ by team{" "}
            <span className="text-red-700">404 Not Found</span>
          </p>
        </div>
      </div>
    </footer>
  );
}
