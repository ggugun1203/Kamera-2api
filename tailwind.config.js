/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        cameraProAmber: '#FFB300',
        cameraProCyan: '#00E5FF',
        cameraProGreen: '#00E676',
        cameraProRed: '#FF5252',
        cameraDark: '#0A0A0C',
        cameraSurface: '#16161A',
        cameraCard: '#1F1F24',
      },
    },
  },
  plugins: [],
};
