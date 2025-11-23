<!--
	README for the ai-assist VS Code extension
	This file documents features, usage, commands, and development steps
	tailored to the ai-assist project in this workspace.
-->

# ai-assist

`ai-assist` is a VS Code extension that helps you capture code snippets from the editor, build an AI-powered context, and interact with an AI assistant directly from a sidebar. Use it to ask questions about your code, request explanations, find bugs, or automatically generate documentation comments for selected code.

**Key capabilities**
- Capture selected code as context (snippet) and keep a short session history.
- Chat with an AI model using your collected snippets as context.
- Actions: explain code, find bugs, and generate documentation (inserts docstrings/comments into files).
- Open and navigate to saved snippets from the sidebar.

**Project**: DocGen (extension id: `ai-assist`)

## Features

- Context capture: press the configured keyboard shortcut to add the current selection to the AI context list in the sidebar.
- Conversational AI: ask questions or run actions (explain, bugs) that use the snippets you've captured.
- Generate docs: insert AI-generated documentation/comment blocks directly above the selected code in the file.
- Snippet navigation: click a snippet in the sidebar to open the file and reveal the original selection.

## Usage

1. Open a file in the editor and select a block of code.
2. Add the selection to the AI context using the keyboard shortcut (default) or run the command:

	 - `aiSidebar.sendSelection` — Capture current selection as a snippet for AI context.

3. Open the AI Assist sidebar (View → AI Assist) to see captured snippets and the chat interface.
4. Use the input box at the bottom of the sidebar to ask the AI questions or click the action buttons (Explain, Find Bugs, Generate Docs).

Notes:
- If you add snippets while the sidebar is closed, they will appear when you open it — the extension sends stored snippets once the webview initializes.
- Generating documentation inserts text into your code files; review edits before saving.

## Keyboard Shortcuts

- Capture selection: `Ctrl+Alt+C` (Windows/Linux) or `Cmd+Alt+C` (macOS) — verify or change in `keybindings.json` / VS Code Keyboard Shortcuts.

You can change or inspect the exact keybinding via the Command Palette: `Preferences: Open Keyboard Shortcuts` and search for `aiSidebar.sendSelection`.

## Commands

- `aiSidebar.sendSelection` — Capture the current editor selection and add it to the sidebar context.
- (Other commands are implemented in the sidebar UI, accessible via the sidebar buttons.)

## Requirements

- This extension requires a running AI backend for conversational features and doc generation if you want real AI responses. By default the project includes a `MockAIService` for local development.
- Node.js (for development tasks such as installing dependencies and running tests).

## Development

To start developing or test locally:

```bash
cd /home/admin1/Desktop/newExtension/ai-assist
npm install
# Run the extension in the Extension Development Host (open in VS Code and press F5)
```

Testing and packaging:

```bash
# Run unit tests (if provided)
npm test

# Package the extension (requires `vsce`)
vsce package
```

## Project structure (high level)

- `sidebar/` — sidebar UI and session management (`SidebarProvider.js`, `SessionManager.js`, `MockAIService.js`)
- `helper/` — small helper modules and API url constants
- `media/` — webview static files (CSS/JS used by the sidebar)
- `extension.js` — extension activation, commands registration
- `package.json` — extension manifest and dependencies

## Contributing

Contributions are welcome. Suggested workflow:

1. Fork the repo and create a feature branch.
2. Implement and test your changes locally using the Extension Development Host.
3. Open a pull request with a clear description and any relevant screenshots.

## Known issues & tips

- The webview needs a moment to initialize; snippets may be stored while the sidebar is closed and delivered once it becomes ready.
- If the extension cannot reach your AI backend, conversational features will show a friendly error and fall back to the mock service if configured.

## License

Include your preferred license here (e.g. MIT) or add a `LICENSE` file to the project root.

---

If you want, I can:
- wire up a basic settings page to configure API endpoints, or
- add a status bar notification when snippets are captured while the sidebar is not ready.

