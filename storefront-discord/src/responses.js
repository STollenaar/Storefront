const querystring = require('querystring');

const renderPage = require('./utils/render-page');

const baseUri = 'https://cheesetown.jackharrhy.com/storefront';

module.exports = async () => ({
	'ping': async (msg) => {
		msg.channel.send('pong!');
	},
	'show': async (msg, command) => {
		const queryPiece = querystring.stringify({ username: command.trim(), simpleUI: '', timestamp: '' });
		const uri = `${baseUri}/?${queryPiece}`;
		const screenshot = await renderPage(uri);

		if (screenshot === null) {
			msg.reply('Unknown user!');
		} else {
			msg.reply('', { files: [screenshot] });
		}
	},
});
