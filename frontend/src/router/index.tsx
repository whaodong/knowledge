import { Navigate, Route, Routes } from 'react-router-dom';
import { AuthLayout } from '../layouts/AuthLayout';
import { AppShellLayout } from '../layouts/AppShellLayout';
import { AuthInitializer } from '../components/auth/AuthInitializer';
import { ProtectedRoute } from '../components/auth/ProtectedRoute';
import { PublicOnlyRoute } from '../components/auth/PublicOnlyRoute';
import { LoginPage } from '../pages/LoginPage';
import { KnowledgePage } from '../pages/KnowledgePage';
import { ChatPage } from '../pages/ChatPage';
import { SessionChatPage } from '../pages/SessionChatPage';
import { SettingsPage } from '../pages/SettingsPage';
import { NotFoundPage } from '../pages/NotFoundPage';

export function AppRouter(): JSX.Element {
  return (
    <AuthInitializer>
      <Routes>
        <Route element={<PublicOnlyRoute />}>
          <Route element={<AuthLayout />}>
            <Route path="/login" element={<LoginPage />} />
          </Route>
        </Route>

        <Route element={<ProtectedRoute />}>
          <Route element={<AppShellLayout />}>
            <Route path="/knowledge" element={<KnowledgePage />} />
            <Route path="/chat" element={<ChatPage />} />
            <Route path="/chat/:sessionId" element={<SessionChatPage />} />
            <Route path="/settings" element={<SettingsPage />} />
          </Route>
        </Route>

        <Route path="/" element={<Navigate to="/knowledge" replace />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </AuthInitializer>
  );
}
