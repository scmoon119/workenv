import { useSelector } from 'react-redux';

import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline, StyledEngineProvider } from '@mui/material';

// routing
import Routes from 'routes';

// defaultTheme
import themes from 'themes';

// project imports
import NavigationScroll from 'layout/NavigationScroll';
import { CookiesProvider } from 'react-cookie';

// ==============================|| APP ||============================== //

const App = () => {
  const customization = useSelector((state) => state.customization);

  console.log(process.env.NODE_ENV);
  console.log(process.env.REACT_APP_API_URL);
  console.log(process.env.REACT_APP_MY_CODE);
  return (
    <StyledEngineProvider injectFirst>
      <ThemeProvider theme={themes(customization)}>
        <CookiesProvider>
          <CssBaseline />
          <NavigationScroll>
            <Routes />
          </NavigationScroll>
        </CookiesProvider>
      </ThemeProvider>
    </StyledEngineProvider>
  );
};

export default App;
