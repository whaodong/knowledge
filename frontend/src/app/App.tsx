import { AppRouter } from '../router';
import { AuthProvider } from '../stores/auth-store';

function App(): JSX.Element {
  return (
    <AuthProvider>
      <AppRouter />
    </AuthProvider>
  );
}

export default App;
