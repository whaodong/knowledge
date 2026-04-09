-- 对齐 Session 登录所需的用户表结构，并写入演示账号
ALTER TABLE IF EXISTS app_user
    ADD COLUMN IF NOT EXISTS password VARCHAR(255),
    ADD COLUMN IF NOT EXISTS nickname VARCHAR(100),
    ADD COLUMN IF NOT EXISTS enabled BOOLEAN;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'app_user'
          AND column_name = 'password_hash'
    ) THEN
        EXECUTE 'UPDATE app_user SET password = COALESCE(password, password_hash) WHERE password IS NULL';
    END IF;
END
$$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'app_user'
          AND column_name = 'status'
    ) THEN
        EXECUTE 'UPDATE app_user SET enabled = COALESCE(enabled, CASE WHEN status = ''ACTIVE'' THEN TRUE ELSE FALSE END) WHERE enabled IS NULL';
    END IF;
END
$$;

UPDATE app_user
SET nickname = COALESCE(nickname, '管理员'),
    enabled = COALESCE(enabled, TRUE),
    password = COALESCE(password, '$2y$10$DAhRArWs1grhaJLzd9BPF.JHf0ODWqFYN/EuRhuxAB5/bYb1Jd5oO'),
    updated_at = CURRENT_TIMESTAMP;

ALTER TABLE app_user
    ALTER COLUMN password SET NOT NULL,
    ALTER COLUMN nickname SET NOT NULL,
    ALTER COLUMN enabled SET NOT NULL;

DO $$
BEGIN
    -- 兼容历史结构：旧字段 password_hash 解除非空约束，避免新代码插入失败
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'app_user'
          AND column_name = 'password_hash'
          AND is_nullable = 'NO'
    ) THEN
        ALTER TABLE app_user ALTER COLUMN password_hash DROP NOT NULL;
    END IF;
END
$$;

DO $$
BEGIN
    -- 若存在历史字段 status，先对历史数据补默认值
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'app_user'
          AND column_name = 'status'
    ) THEN
        UPDATE app_user
        SET status = COALESCE(status, 'ACTIVE')
        WHERE status IS NULL;
    END IF;

    -- 根据历史字段存在性动态插入演示账号，避免旧约束冲突
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'app_user'
          AND column_name = 'password_hash'
    ) AND EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'app_user'
          AND column_name = 'status'
    ) THEN
        INSERT INTO app_user (username, password, password_hash, nickname, enabled, status, created_at, updated_at)
        SELECT 'admin',
               '$2y$10$DAhRArWs1grhaJLzd9BPF.JHf0ODWqFYN/EuRhuxAB5/bYb1Jd5oO',
               '$2y$10$DAhRArWs1grhaJLzd9BPF.JHf0ODWqFYN/EuRhuxAB5/bYb1Jd5oO',
               '管理员',
               TRUE,
               'ACTIVE',
               CURRENT_TIMESTAMP,
               CURRENT_TIMESTAMP
        WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE username = 'admin');
    ELSE
        INSERT INTO app_user (username, password, nickname, enabled, created_at, updated_at)
        SELECT 'admin',
               '$2y$10$DAhRArWs1grhaJLzd9BPF.JHf0ODWqFYN/EuRhuxAB5/bYb1Jd5oO',
               '管理员',
               TRUE,
               CURRENT_TIMESTAMP,
               CURRENT_TIMESTAMP
        WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE username = 'admin');
    END IF;

    -- 若存在历史字段 password_hash/status，同步演示账号，保证老数据兼容
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'app_user'
          AND column_name = 'password_hash'
    ) THEN
        UPDATE app_user
        SET password_hash = COALESCE(password_hash, password)
        WHERE username = 'admin';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'app_user'
          AND column_name = 'status'
    ) THEN
        UPDATE app_user
        SET status = COALESCE(status, 'ACTIVE')
        WHERE username = 'admin';
    END IF;
END
$$;
