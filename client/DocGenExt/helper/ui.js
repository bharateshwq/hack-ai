const vscode = require('vscode');
const changeProgressColor = () => {
	const workbenchConfig = vscode.workspace.getConfiguration('workbench');
	const currentColorScheme = workbenchConfig.get('colorCustomizations');
	const mintlifyColorScheme = {
		"[*Dark*]": {
			"progressBar.background": "#0D9373",
			"notificationsInfoIcon.foreground": "#FFFF00",
			"editor.selectionBackground": "#0D937333"
		},
		"[*Light*]": {
			"progressBar.background": "#0D9373",
			"notificationsInfoIcon.foreground": "#FFFF00",
			"editor.selectionBackground": "#ffffffff"
		}
	};
	workbenchConfig.update('colorCustomizations', { ...currentColorScheme, ...mintlifyColorScheme }, true);
};

const removeProgressColor = () => {
	const workbenchConfig = vscode.workspace.getConfiguration('workbench');
	const currentColorScheme = workbenchConfig.get('colorCustomizations');
	const { ['[*Dark*]']: defaultDark, ['[*Light*]']: defaultLight, ...removedScheme } = currentColorScheme;
	workbenchConfig.update('colorCustomizations', removedScheme, true);
};

module.exports = {
	changeProgressColor,
	removeProgressColor
};