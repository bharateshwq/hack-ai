// The module 'vscode' contains the VS Code extensibility API
const axios = require('axios');
const { GENERATE_DOCS_URL, GENERATE_DOCS_WITHOUT_SELECTION_URL, USERID } = require('./helper/api');
// FIX: Import changeProgressColor alongside removeProgressColor
const { removeProgressColor, changeProgressColor } = require('./helper/ui');
const { getHighlightedText, getWidth } = require('./helper/utils');

// Import the module and reference it with the alias vscode in your code below
const vscode = require('vscode');

// This method is called when your extension is activated
// Your extension is activated the very first time the command is executed

/**
 * @param {vscode.ExtensionContext} context
 */
function activate(context) {

	// Use the console to output diagnostic information (console.log) and errors (console.error)
	// This line of code will only be executed once when your extension is activated
	console.log('Congratulations, your extension "ai-assist" is now active!');

	// The command has been defined in the package.json file
	// Now provide the implementation of the command with  registerCommand
	// The commandId parameter must match the command field in package.json
	const disposable = vscode.commands.registerCommand('ai-assist.helloWorld', function () {
		// The code you place here will be executed every time your command is executed

		// Display a message box to the user
		vscode.window.showInformationMessage('Hello World from AI-Assist!');
	});

	context.subscriptions.push(disposable);

	// Flag to prevent multiple simultaneous doc generations
	let isGeneratingDocs = false;

	const writeDocs = vscode.commands.registerCommand('ai-assist.write_docs', async function (args) {

		// Prevent multiple simultaneous doc generations
		if (isGeneratingDocs) {
			vscode.window.showWarningMessage('⏳ Documentation generation is already in progress. Please wait for it to complete.');
			return null;
		}

		isGeneratingDocs = true;

		changeProgressColor();
		const editor = vscode.window.activeTextEditor;
		if (editor == null) {
			removeProgressColor();
			isGeneratingDocs = false; // Reset flag
			return;
		}

		const { languageId, getText, fileName } = editor.document;

		let selection;
		let highlighted;
		let location = null;
		let line = null;

		if (args && args.position) {
			// Called from hover
			console.log('write_docs called with args:', args);
			const document = editor.document;
			// args.position is likely a plain object from JSON, convert to vscode.Position
			const position = new vscode.Position(args.position.line, args.position.character);

			// Fetch all symbols in the document
			const symbols = await vscode.commands.executeCommand('vscode.executeDocumentSymbolProvider', document.uri);

			// Helper to find the most specific symbol containing the position
			const findSymbol = (symbols, pos) => {
				for (const symbol of symbols) {
					if (symbol.range.contains(pos)) {
						const child = findSymbol(symbol.children, pos);
						return child || symbol;
					}
				}
				return null;
			};

			const symbol = findSymbol(symbols || [], position);

			if (symbol && (symbol.kind === vscode.SymbolKind.Function || symbol.kind === vscode.SymbolKind.Method || symbol.kind === vscode.SymbolKind.Constant)) {
				// Use the symbol's range to get the full function body
				highlighted = document.getText(symbol.range);
				location = document.offsetAt(symbol.range.start);
				line = document.lineAt(symbol.range.start.line);

				// Set selection to the start of the symbol
				selection = new vscode.Selection(symbol.range.start, symbol.range.start);
			} else {
				// Fallback if no symbol found (e.g. language server not ready)
				line = document.lineAt(position.line);
				highlighted = line.text;
				location = document.offsetAt(position);
				selection = new vscode.Selection(position.line, 0, position.line, 0);
			}

		} else {
			// Called from command palette or keybinding
			const result = getHighlightedText(editor);
			selection = result.selection;
			highlighted = result.highlighted;

			if (!highlighted) {
				removeProgressColor();
				let document = editor.document;
				let curPos = editor.selection.active;

				// Try to find symbol at cursor to be smart
				const symbols = await vscode.commands.executeCommand('vscode.executeDocumentSymbolProvider', document.uri);
				const findSymbol = (symbols, pos) => {
					for (const symbol of symbols) {
						if (symbol.range.contains(pos)) {
							const child = findSymbol(symbol.children, pos);
							return child || symbol;
						}
					}
					return null;
				};
				const symbol = findSymbol(symbols || [], curPos);

				if (symbol && (symbol.kind === vscode.SymbolKind.Function || symbol.kind === vscode.SymbolKind.Method || symbol.kind === vscode.SymbolKind.Constant)) {
					highlighted = document.getText(symbol.range);
					location = document.offsetAt(symbol.range.start);
					line = document.lineAt(symbol.range.start.line);
					selection = new vscode.Selection(symbol.range.start, symbol.range.start);
				} else {
					// Fallback to line
					location = document.offsetAt(curPos);
					line = document.lineAt(curPos);
					if (line.isEmptyOrWhitespace) {
						vscode.window.showErrorMessage(`Please select a line with code and try again`);
						return;
					}
					highlighted = line.text;
				}
			}
		}

		vscode.window.withProgress({
			location: vscode.ProgressLocation.Notification,
			title: 'Generating documentation',
		}, async () => {
			try {
				const WRITE_ENDPOINT = highlighted ? GENERATE_DOCS_URL : GENERATE_DOCS_WITHOUT_SELECTION_URL;

				// Call the API (fire and forget or wait for echo)
				const response = await axios.post(WRITE_ENDPOINT, {
					language: languageId,
					fileName,
					commented: true,
					userId: USERID,
					source: 'vscode',
					// context: getText(),
					width: line ? getWidth(line.firstNonWhitespaceCharacterIndex) : getWidth(selection.start.character),
					code: highlighted,
					location,
					line: line?.text,
					// query:highlighted
				});
				console.log('API response:', response.data);

				const docstring = response.data.ai_response + '\n'

				await editor.edit(editBuilder => {
					if (selection) {
						editBuilder.insert(selection.start, docstring);
					}
				});

				removeProgressColor();
				vscode.window.showInformationMessage('Documentation generated!');
				isGeneratingDocs = false; // Reset flag
				return docstring;

			} catch (err) {
				removeProgressColor();
				console.error(err);
				vscode.window.showErrorMessage('Error generating documentation');
				isGeneratingDocs = false; // Reset flag
				return null;
			}
		});
	});

	context.subscriptions.push(writeDocs);

	// Register HoverProvider
	const languages = ['javascript', 'typescript', 'javascriptreact', 'typescriptreact'];

	languages.forEach(lang => {
		const hoverProvider = vscode.languages.registerHoverProvider(lang, {
			provideHover(document, position, token) {
				const range = document.getWordRangeAtPosition(position);
				const word = document.getText(range);
				const line = document.lineAt(position).text;

				// Regex to check for function definitions
				// Matches: 
				// - function foo()
				// - const foo = () =>
				// - const foo = function()
				// - const foo = async () =>
				// - export const foo = () =>
				// - public foo() (in classes)
				// - foo = () => (class properties)
				const functionRegex = /function\s+\w+|const\s+\w+\s*=\s*(async\s*)?(\(\)|function)|(\w+\s*=\s*(async\s*)?(\(\)|function))|((async\s+)?\w+\s*\(.*\))/;

				if (functionRegex.test(line) && range) {
					// Check if the hovered word is part of the function definition
					// This is a heuristic.
					if (line.includes(word)) {
						const args = { position: position };
						const commandUri = vscode.Uri.parse(
							`command:ai-assist.write_docs?${encodeURIComponent(JSON.stringify(args))}`
						);
						const contents = new vscode.MarkdownString(`[Generate Docs](${commandUri})`);
						contents.isTrusted = true;
						return new vscode.Hover(contents);
					}
				}
				return null;
			}
		});
		context.subscriptions.push(hoverProvider);
	});

	// Register Sidebar Provider
	const SidebarProvider = require('./sidebar/SidebarProvider');
	const sidebarProvider = new SidebarProvider(context.extensionUri, context);
	context.subscriptions.push(
		vscode.window.registerWebviewViewProvider("aiSidebar", sidebarProvider)
	);

	context.subscriptions.push(
		vscode.commands.registerCommand('aiSidebar.open', () => {
			vscode.commands.executeCommand('workbench.view.extension.ai-sidebar-view');
		})
	);



	// Auto-capture disabled in favor of manual shortcut
	// let selectionTimeout;
	// vscode.window.onDidChangeTextEditorSelection(event => { ... });

}

// This method is called when your extension is deactivated
function deactivate() { }

module.exports = {
	activate,
	deactivate
}
