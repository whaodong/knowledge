import { FormEvent, useMemo, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../stores/auth-store';
import { normalizeErrorMessage } from '../utils/error-message';

const DEFAULT_AFTER_LOGIN_PATH = '/knowledge';

export function LoginPage(): JSX.Element {
  const navigate = useNavigate();
  const location = useLocation();
  const {
    state: { isLoggingIn, errorMessage },
    loginWithPassword,
    clearError
  } = useAuth();

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [validationMessage, setValidationMessage] = useState('');

  const redirectPath = useMemo(() => {
    const searchParams = new URLSearchParams(location.search);
    return searchParams.get('redirect') || DEFAULT_AFTER_LOGIN_PATH;
  }, [location.search]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>): Promise<void> => {
    event.preventDefault();
    clearError();

    if (!username.trim() || !password.trim()) {
      setValidationMessage('请输入用户名和密码');
      return;
    }

    setValidationMessage('');

    try {
      await loginWithPassword({ username: username.trim(), password });
      navigate(redirectPath, { replace: true });
    } catch (error) {
      setValidationMessage(normalizeErrorMessage(error, '登录失败，请稍后重试'));
    }
  };

  return (
    <div>
      <h1 className="form-title">登录</h1>
      <p className="form-subtitle">使用账号密码登录，基于 Session 保持登录态。</p>
      {validationMessage ? <p className="gateway-alert">{validationMessage}</p> : null}
      {!validationMessage && errorMessage ? <p className="gateway-alert">{errorMessage}</p> : null}
      <form className="halo-form" onSubmit={handleSubmit} noValidate>
        <div className="form-item">
          <label htmlFor="username">用户名或邮箱地址</label>
          <div className="form-input">
            <input
              id="username"
              value={username}
              onChange={(event) => setUsername(event.target.value)}
              autoComplete="username"
            />
          </div>
        </div>
        <div className="form-item">
          <div className="form-label-group">
            <label htmlFor="password">密码</label>
            <a className="form-item-extra-link" href="#" onClick={(event) => event.preventDefault()}>
              忘记密码?
            </a>
          </div>
          <div className="form-input">
            <input
              id="password"
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              autoComplete="current-password"
            />
          </div>
        </div>
        <button type="submit" disabled={isLoggingIn}>
          {isLoggingIn ? '登录中...' : '登录'}
        </button>
      </form>
    </div>
  );
}
