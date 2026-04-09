import '../styles/dashboard.css';

export default function App() {
  return (
    <>
      {/* Top Navigation - TryHackMe Style */}
      <nav className="thm-nav">
        <div className="thm-nav__container">
          {/* Logo */}
          <div className="thm-nav__logo">
            <svg viewBox="0 0 24 24" fill="currentColor">
              <circle cx="12" cy="12" r="10" opacity="0.2"/>
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
            </svg>
          </div>

          {/* Navigation Menu */}
          <div className="thm-nav__menu">
            <a href="#" className="thm-nav__link thm-nav__link--active">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <rect x="3" y="4" width="18" height="12" rx="2" ry="2"></rect>
                <path d="M8 20h8"></path>
                <path d="M12 16v4"></path>
              </svg>
              <span>Dashboard</span>
            </a>

            <a href="#" className="thm-nav__link">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"></path>
                <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"></path>
              </svg>
              <span>Learn</span>
            </a>

            <a href="#" className="thm-nav__link">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M9 3H5a2 2 0 0 0-2 2v4"></path>
                <path d="M15 3h4a2 2 0 0 1 2 2v4"></path>
                <path d="M9 21H5a2 2 0 0 1-2-2v-4"></path>
                <path d="M15 21h4a2 2 0 0 0 2-2v-4"></path>
                <circle cx="12" cy="12" r="3"></circle>
              </svg>
              <span>Practice</span>
            </a>

            <a href="#" className="thm-nav__link">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"></path>
                <path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"></path>
                <path d="M4 22h16"></path>
                <path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"></path>
                <path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"></path>
                <path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"></path>
              </svg>
              <span>Compete</span>
            </a>
          </div>

          {/* Right Actions */}
          <div className="thm-nav__actions">
            <button className="thm-nav__icon-btn">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="11" cy="11" r="8"></circle>
                <path d="m21 21-4.35-4.35"></path>
              </svg>
            </button>

            <button className="thm-nav__icon-btn">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
                <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
              </svg>
            </button>

            <img
              src="https://api.dicebear.com/7.x/avataaars/svg?seed=Duy&backgroundColor=ff6b35"
              alt="User"
              className="thm-nav__avatar"
            />

            <button className="thm-nav__logout">Logout</button>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="thm-main">
        <div className="thm-dashboard">

          {/* LEFT COLUMN - Main Content */}
          <div>
            {/* Welcome Card */}
            <div className="thm-card thm-welcome">
              <div className="thm-welcome__avatar">👋</div>
              <div className="thm-welcome__text">
                <h2>Hey Duy!</h2>
                <p>You can see an overview of your learning progress here!</p>
              </div>
            </div>

            {/* Main Mission Card */}
            <div className="thm-card">
              <p className="thm-mission__title">
                Complete your first room to unlock your dashboard and boost your chances of reaching your goals!
              </p>

              {/* Introduction Section */}
              <div className="thm-intro">
                <svg className="thm-intro__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                  <polyline points="14 2 14 8 20 8"></polyline>
                  <line x1="16" y1="13" x2="8" y2="13"></line>
                  <line x1="16" y1="17" x2="8" y2="17"></line>
                  <polyline points="10 9 9 9 8 9"></polyline>
                </svg>

                <div className="thm-intro__content">
                  <div className="thm-intro__header">
                    <h3 className="thm-intro__title">Introduction</h3>
                    <span className="thm-intro__badge">NEW</span>
                  </div>

                  <p className="thm-intro__desc">
                    Make sure you grasp the basics yourself to ensure you have the minimum foundation to participate in the course!
                  </p>

                  <button className="thm-intro__btn">Complete my first room</button>
                </div>
              </div>

              {/* Learning Types List */}
              <div className="thm-types">
                <div className="thm-type">
                  <div className="thm-type__icon">📖</div>
                  <div className="thm-type__content">
                    <h3>Knowledge Only (What you need to know)</h3>
                    <p>
                      Open and complete course modules and test yourself—understand what the issue is and why it matters.
                    </p>
                  </div>
                </div>

                <div className="thm-type">
                  <div className="thm-type__icon">✅</div>
                  <div className="thm-type__content">
                    <h3>Knowledge Check Only (How to test understanding)</h3>
                    <p>
                      Answer questions or quizzes to verify understanding of concepts, risks, and correct mitigation choices.
                    </p>
                  </div>
                </div>

                <div className="thm-type">
                  <div className="thm-type__icon">🛠️</div>
                  <div className="thm-type__content">
                    <h3>Practice Only (Hands-on activities)</h3>
                    <p>
                      Do hands-on labs to identify, exploit vulnerability, and fix vulnerabilities using real tools and scenarios.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* RIGHT COLUMN - Sidebar */}
          <aside className="thm-sidebar">

            {/* Weekly Mission Card */}
            <div className="thm-card thm-card--mint thm-weekly">
              <span className="thm-weekly__badge">5 days to next mission</span>

              <div className="thm-weekly__header">
                <h2 className="thm-weekly__title">Weekly Mission</h2>
                <p className="thm-weekly__subtitle">What's in the class! 🎧</p>
              </div>

              <div className="thm-weekly__divider"></div>

              <div className="thm-weekly__goals">
                <div className="thm-goal">
                  <span className="thm-goal__label">Answer Questions</span>
                  <span className="thm-goal__value">0 / 6</span>
                </div>

                <div className="thm-goal">
                  <span className="thm-goal__label">Earn Points</span>
                  <span className="thm-goal__value">0 / 407</span>
                </div>

                <div className="thm-goal">
                  <span className="thm-goal__label">Complete Rooms</span>
                  <span className="thm-goal__value">0 / 7</span>
                </div>
              </div>
            </div>

            {/* Questions Answered Card */}
            <div className="thm-card">
              <h3 className="thm-stats__header">0 Questions answered</h3>
              <p className="thm-stats__subtext">this week</p>
            </div>

            {/* Profile CTA Card */}
            <div className="thm-card">
              <div className="thm-profile-cta">
                <h3 className="thm-profile-cta__title">Your Stats</h3>
                <a href="#" className="thm-profile-cta__link">Go to profile</a>
              </div>

              <div className="thm-profile-cta__message">
                <span className="thm-profile-cta__icon">🔒</span>
                <p className="thm-profile-cta__text">
                  Complete the first room of your path to unlock this feature.
                </p>
              </div>
            </div>

          </aside>

        </div>
      </main>
    </>
  );
}
