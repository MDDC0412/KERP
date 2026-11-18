const SESSION_KEY = 'kerp-session';

export interface SessionProfile {
  accessToken: string;
  userId: number;
  username: string;
  displayName: string;
  roles: string[];
  permissions: string[];
}

export function readSession() {
  const raw = window.localStorage.getItem(SESSION_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as SessionProfile;
  } catch {
    window.localStorage.removeItem(SESSION_KEY);
    return null;
  }
}

export function saveSession(session: SessionProfile) {
  window.localStorage.setItem(SESSION_KEY, JSON.stringify(session));
}

export function clearSession() {
  window.localStorage.removeItem(SESSION_KEY);
}
