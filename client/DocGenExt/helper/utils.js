const vscode = require('vscode');
const getHighlightedText = (editor) => {
  const { selection } = editor;
  const highlightRange = new vscode.Range(editor.selection.start, editor.selection.end);
  const highlighted = editor.document.getText(highlightRange);
  return { selection, highlighted };
};

const getFileExtension = (filename) => {
  const fileExtensionRegex = /(?:\.([^.]+))?$/;
  const fileExtension = fileExtensionRegex.exec(filename);
  if (fileExtension == null || fileExtension.length === 0) {
    return '';
  }
  return fileExtension[1];
};


const getWidth = (offset) => {
  const rulers = vscode.workspace.getConfiguration('editor').get('rulers');
  const maxWidth = rulers != null && rulers.length > 0 ? rulers[0] : 100;
  const width = maxWidth - offset;
  return width;
};

module.exports = {
  getHighlightedText,
  getFileExtension,
  getWidth
};