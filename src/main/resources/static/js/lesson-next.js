(function () {
  const lessons = [
    ['A01-broken-access-control', 'A02: Security Misconfiguration'],
    ['A02-security-misconfiguration', 'A03: Software Supply Chain Failures'],
    ['A03-software-supply-chain-failures', 'A04: Cryptographic Failures'],
    ['A04-cryptographic-failures', 'A05: Injection'],
    ['A05-injection', 'A06: Insecure Design'],
    ['A06-insecure-design', 'A07: Authentication Failures'],
    ['A07-authentication-failures', 'A08: Software or Data Integrity Failures'],
    ['A08-software-or-data-integrity-failures', 'A09: Security Logging and Monitoring Failures'],
    ['A09-security-logging-and-monitoring-failures', 'A10: Server-Side Request Forgery'],
    ['A10-server-side-request-forgery', 'Back to learning path']
  ];

  document.addEventListener('DOMContentLoaded', function () {
    const main = document.querySelector('main.APage');
    if (!main) {
      return;
    }
    if (main.querySelector('.ALessonNext')) {
      return;
    }

    const slug = decodeURIComponent(window.location.pathname.split('/').pop() || '');
    const currentIndex = lessons.findIndex(function (lesson) {
      return lesson[0] === slug;
    });
    if (currentIndex === -1) {
      return;
    }

    const isLast = currentIndex === lessons.length - 1;
    const href = isLast ? '/learn' : '/learn/' + lessons[currentIndex + 1][0];
    const label = isLast ? lessons[currentIndex][1] : 'Next lesson: ' + lessons[currentIndex][1];

    const nav = document.createElement('nav');
    nav.className = 'ALessonNext';
    nav.setAttribute('aria-label', 'Lesson navigation');

    const link = document.createElement('a');
    link.className = 'ALessonNext__button';
    link.href = href;
    link.textContent = label;

    nav.appendChild(link);
    main.appendChild(nav);
  });
})();
