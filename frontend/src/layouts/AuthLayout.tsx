import { Outlet } from 'react-router-dom';
import { motion } from 'motion/react';

export function AuthLayout(): JSX.Element {
  return (
    <div className="gateway-page">
      <motion.div
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.2 }}
        className="gateway-wrapper"
      >
        <div className="halo-form-wrapper">
          <Outlet />
        </div>
      </motion.div>
    </div>
  );
}
