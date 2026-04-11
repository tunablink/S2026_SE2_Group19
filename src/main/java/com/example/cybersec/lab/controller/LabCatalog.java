package com.example.cybersec.lab.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class LabCatalog {
    private static final List<LabModule> MODULES = List.of(
            module("a01", "A01", "Broken Access Control",
                    lab("idor", "A01_IDOR_IDENTIFY", "Identify an IDOR/BOLA in a demo app", "Read-only", "10 min",
                            "You are signed in as Linh. The UI only lists ACC-101, but the statement endpoint accepts an account ID from the request.",
                            facts("Current user", "linh@example.test", "Owned object", "ACC-101", "Target object", "ACC-102"),
                            List.of("Send the allowed request for ACC-101.", "Change only the object ID to ACC-102.", "Capture the 200 OK response and exposed statement data."),
                            "GET /api/demo/accounts/ACC-102/statement",
                            "IDOR/BOLA finding: changing account ID from ACC-101 to ACC-102 returned 200 OK and exposed another user's statement data."),
                    lab("fix", "A01_IDOR_FIX", "Fix with middleware, ownership checks, and retest", "Defense", "10 min",
                            "The handler returns a statement before proving the authenticated user owns the requested account.",
                            facts("Current user", "linh@example.test", "Blocked object", "ACC-102", "Expected result", "403 Forbidden"),
                            List.of("Add authentication and object loading middleware.", "Compare the account owner with the current principal.", "Retest the same ACC-102 request and verify it is denied."),
                            "app.get('/api/accounts/:accountId/statement', requireAuth, loadAccount, enforceOwnership, getStatement);",
                            "Middleware enforceOwnership compares ownerId with req.user.id, returns 403 Forbidden on mismatch, and retest confirms the same request is blocked.")),
            module("a02", "A02", "Security Misconfiguration",
                    lab("debug-leak", "A02_DEBUG_LEAK", "Find a debug endpoint and sensitive error leak", "Discovery", "10 min",
                            "A staging endpoint was accidentally exposed in production and verbose errors reveal configuration details.",
                            facts("Endpoint", "/debug/env", "Risk", "Secrets and stack traces", "User role", "Regular learner"),
                            List.of("Probe the debug endpoint.", "Trigger a controlled error.", "Record the leaked setting or stack trace."),
                            "GET /debug/env\nGET /api/orders/demo?format=broken",
                            "Security misconfiguration evidence: exposed /debug/env returned environment data and verbose error output leaked stack trace details."),
                    lab("baseline", "A02_BASELINE_RETEST", "Apply a baseline and retest", "Defense", "10 min",
                            "The app needs a production baseline that disables debug routes, genericizes errors, and sets secure headers.",
                            facts("Debug routes", "Disabled", "Errors", "Generic response", "Headers", "CSP, HSTS, X-Content-Type-Options"),
                            List.of("Disable debug endpoints outside local development.", "Replace verbose errors with a generic error handler.", "Retest the old debug URL and confirm it is blocked."),
                            "security.baseline=production\nmanagement.endpoints.web.exposure.include=health,info",
                            "Production baseline disables debug endpoints, uses generic error handling, adds secure headers, and retest confirms /debug/env is blocked.")),
            module("a03", "A03", "Software Supply Chain Failures",
                    lab("risky-dependency", "A03_RISKY_DEPENDENCY", "Detect a risky dependency change in a PR", "Review", "10 min",
                            "A pull request updates an indirect package and adds an install script that was not present before.",
                            facts("Changed package", "image-helper", "New behavior", "postinstall script", "Risk", "Unreviewed transitive code"),
                            List.of("Review the dependency diff.", "Identify the risky version or script change.", "Record why the change should be blocked pending review."),
                            "package-lock.json: image-helper 1.4.2 -> 1.4.3\nscripts.postinstall = node scripts/fetch.js",
                            "Supply chain evidence: risky dependency change introduced a postinstall script in image-helper 1.4.3 and should be blocked for review."),
                    lab("pinning-gates", "A03_PINNING_GATES", "Add pinning, review gates, and verify", "Defense", "10 min",
                            "The build should only consume reviewed, pinned dependencies and fail when lockfiles or provenance checks are missing.",
                            facts("Control", "Pinned versions", "Gate", "Dependency review", "Verification", "CI fails unsafe PR"),
                            List.of("Pin dependency versions and commit the lockfile.", "Require dependency review in CI.", "Verify the risky PR fails the gate."),
                            "ci: dependency-review + lockfile check + provenance verification",
                            "Pinned dependencies, lockfile enforcement, dependency review gate, and CI verification block the risky package change.")),
            module("a04", "A04", "Cryptographic Failures",
                    lab("sensitive-data", "A04_SENSITIVE_DATA", "Identify sensitive data stored without protection", "Discovery", "10 min",
                            "A profile export stores personal data and recovery tokens in plaintext.",
                            facts("Data", "Email, phone, recovery token", "Storage", "Plaintext", "Impact", "Disclosure after database leak"),
                            List.of("Inspect the stored record.", "Mark which fields are sensitive.", "Explain the impact of plaintext storage."),
                            "user_export: { email, phone, recoveryToken: 'reset-9241' }",
                            "Cryptographic failure evidence: recovery token and personal data are stored in plaintext, creating disclosure risk after database access."),
                    lab("encryption-key-handling", "A04_ENCRYPTION_RETEST", "Apply encryption, key handling, and retest", "Defense", "10 min",
                            "Sensitive values must be encrypted with managed keys and tokens should be hashed or short-lived.",
                            facts("Encryption", "AES-GCM or managed KMS", "Keys", "Rotatable and not in code", "Retest", "No plaintext token"),
                            List.of("Encrypt sensitive fields with authenticated encryption.", "Move keys to a managed secret or KMS.", "Retest storage and confirm plaintext values are gone."),
                            "encrypt(recoveryToken, kmsKeyId)\nstore tokenHash instead of raw token",
                            "Encrypted sensitive fields with managed KMS keys, hashed recovery tokens, and retest confirms no plaintext secret remains.")),
            module("a05", "A05", "Injection",
                    lab("confirm-injection", "A05_CONFIRM_INJECTION", "Find and confirm an injection flaw in a demo query", "Exploit", "10 min",
                            "A search endpoint concatenates user input into a SQL query.",
                            facts("Input", "search term", "Payload", "' OR '1'='1", "Risk", "Unauthorized rows returned"),
                            List.of("Submit a normal search.", "Submit a safe training SQLi payload.", "Capture the changed result set."),
                            "SELECT * FROM products WHERE name = '<input>'",
                            "Injection evidence: SQLi payload ' OR '1'='1 changed the query behavior and returned unauthorized rows."),
                    lab("parameterization", "A05_PARAMETERIZATION", "Fix with parameterization and retest", "Defense", "10 min",
                            "The query should bind input as data, not concatenate it into executable SQL.",
                            facts("Fix", "Prepared statement", "Input handling", "Bound parameter", "Retest", "Payload treated as text"),
                            List.of("Replace string concatenation with parameters.", "Keep validation separate from query construction.", "Retest the same payload and confirm no extra rows return."),
                            "PreparedStatement ps = conn.prepareStatement('SELECT * FROM products WHERE name = ?');",
                            "PreparedStatement parameterization binds input safely, retest with ' OR '1'='1 returns no unauthorized rows.")),
            module("a06", "A06", "Insecure Design",
                    lab("abuse-cases", "A06_ABUSE_CASES", "Map abuse cases and missing controls for a demo workflow", "Design", "10 min",
                            "A coupon workflow lets users repeatedly apply trial credits because the design lacks abuse-case controls.",
                            facts("Workflow", "Trial coupon redemption", "Missing control", "One redemption per user", "Impact", "Fraud and credit abuse"),
                            List.of("Map the intended workflow.", "List at least two abuse cases.", "Identify the missing preventive control."),
                            "POST /api/coupons/TRIAL/apply repeated with the same account",
                            "Insecure design evidence: repeated trial coupon redemption lacks one-per-user control and enables abuse cases such as credit farming."),
                    lab("design-controls", "A06_DESIGN_CONTROLS", "Add design controls and verify the workflow", "Defense", "10 min",
                            "The design needs explicit limits, state transitions, and abuse-case tests.",
                            facts("Control", "Rate limit and one-time redemption", "Workflow", "State machine", "Verification", "Abuse-case tests"),
                            List.of("Add a stateful one-redemption rule.", "Rate limit repeated attempts.", "Verify abuse-case tests fail closed."),
                            "couponPolicy.enforceOneRedemptionPerUser(userId, couponId)",
                            "Design controls add rate limit, one-redemption workflow state, and abuse-case tests that verify repeated redemption is denied.")),
            module("a07", "A07", "Authentication Failures",
                    lab("rate-limit-lockout", "A07_RATE_LIMIT_LOCKOUT", "Add rate limit and lockout for login", "Defense", "10 min",
                            "The login endpoint allows unlimited password attempts against the same account.",
                            facts("Endpoint", "/login", "Attack", "Brute force", "Control", "Rate limit and temporary lockout"),
                            List.of("Define a per-account and per-IP attempt limit.", "Lock the account temporarily after repeated failures.", "Log the lockout event."),
                            "loginPolicy.maxFailures=5\nloginPolicy.lockout=15m",
                            "Authentication defense adds per-IP and per-account rate limit, temporary lockout after failures, and logs the lockout event."),
                    lab("session-hardening", "A07_SESSION_HARDENING", "Harden sessions and retest", "Defense", "10 min",
                            "Sessions persist too long and cookies are missing security attributes.",
                            facts("Cookie", "HttpOnly, Secure, SameSite", "Session", "Rotation after login", "Retest", "Old session invalid"),
                            List.of("Set secure cookie attributes.", "Rotate the session ID after login.", "Retest that old sessions are invalidated."),
                            "Set-Cookie: SID=...; HttpOnly; Secure; SameSite=Lax",
                            "Session hardening sets HttpOnly Secure SameSite cookies, rotates session ID after login, and retest invalidates the old session.")),
            module("a08", "A08", "Software or Data Integrity Failures",
                    lab("update-integrity", "A08_UPDATE_INTEGRITY", "Add integrity verification for an update flow", "Defense", "10 min",
                            "The updater downloads a package without verifying a signature or checksum.",
                            facts("Flow", "Auto update", "Missing control", "Signature verification", "Impact", "Tampered package installed"),
                            List.of("Record the unverified download behavior.", "Add checksum or signature verification.", "Verify a tampered package is rejected."),
                            "download update.zip -> install without signature check",
                            "Integrity control verifies package signature and checksum before install, and retest rejects a tampered update package."),
                    lab("deserialization", "A08_DESERIALIZATION", "Harden deserialization and retest", "Defense", "10 min",
                            "The app deserializes untrusted job data into broad object types.",
                            facts("Input", "Untrusted job payload", "Risk", "Unsafe deserialization", "Control", "Allowlist schema"),
                            List.of("Identify the unsafe deserialization boundary.", "Replace broad object loading with an allowlisted DTO schema.", "Retest a disallowed type and verify rejection."),
                            "deserialize(request.body) -> Object",
                            "Deserialization hardening uses an allowlist DTO schema, rejects disallowed types, and retest confirms unsafe payload is blocked.")),
            module("a09", "A09", "Security Logging and Monitoring Failures",
                    lab("structured-logs", "A09_STRUCTURED_LOGS", "Add structured logs to key security events", "Defense", "7 min",
                            "Authentication and authorization decisions are not logged with enough context for investigation.",
                            facts("Events", "Login failure, access denied", "Format", "Structured JSON", "Fields", "userId, ip, action, result"),
                            List.of("Choose key security events.", "Log structured fields without secrets.", "Verify logs include result and correlation ID."),
                            "{\"event\":\"access_denied\",\"userId\":\"101\",\"action\":\"read_statement\",\"result\":\"denied\"}",
                            "Structured logs capture login failure and access denied events with userId, ip, action, result, and correlation ID without secrets."),
                    lab("alert-rules", "A09_ALERT_RULES", "Write 3 alert rules with suppression", "Detection", "7 min",
                            "The team needs actionable alerts for brute force, privilege changes, and repeated denied object access.",
                            facts("Rules", "3", "Suppression", "Required", "Noise target", "Actionable alerts"),
                            List.of("Write three alert rules.", "Add a suppression window.", "Explain why each alert is actionable."),
                            "alert when failed_login count by account > 10 in 5m suppress 30m",
                            "Alert rules cover brute force, privilege change, and repeated access denied events with suppression windows to reduce noise."),
                    lab("validate-alerts", "A09_VALIDATE_ALERTS", "Validate alerts using simulated brute-force and access-denied events", "Validation", "6 min",
                            "Alert rules only count if simulated attacks trigger them and normal activity stays quiet.",
                            facts("Simulation", "Brute force and access denied", "Expected", "Alerts fire", "Control", "False-positive check"),
                            List.of("Replay failed login events.", "Replay repeated denied object access.", "Confirm alerts fire with suppression and normal traffic stays quiet."),
                            "simulate 12 failed_login events for one account in 5 minutes",
                            "Validation replayed brute-force and access-denied events, alerts fired, suppression worked, and normal traffic did not alert.")),
            module("a10", "A10", "Server-Side Request Forgery and Exceptional Conditions",
                    lab("safe-errors", "A10_SAFE_ERRORS", "Replace verbose errors with safe responses", "Defense", "7 min",
                            "A backend integration exposes internal hostnames and stack traces when dependency calls fail.",
                            facts("Failure", "Dependency error", "Leak", "Internal hostname", "Expected", "Safe generic response"),
                            List.of("Trigger a dependency failure.", "Replace the verbose error with a safe response.", "Log detailed context server-side only."),
                            "500 java.net.ConnectException api-internal.service.local",
                            "Safe error handling returns generic responses, logs detailed dependency failure server-side, and removes internal hostname leaks."),
                    lab("fail-closed", "A10_FAIL_CLOSED", "Add fail-closed behavior for auth and permission checks", "Defense", "7 min",
                            "If the permission service times out, the app currently allows the request.",
                            facts("Dependency", "Permission service", "Bad behavior", "Allow on timeout", "Expected", "Deny by default"),
                            List.of("Identify the allow-on-error branch.", "Change it to deny by default.", "Retest timeout and verify access is denied."),
                            "catch TimeoutException -> allow",
                            "Fail-closed permission check denies by default on timeout or error, returns 403, and retest confirms access is blocked."),
                    lab("timeouts-circuit-breaker", "A10_TIMEOUTS_CIRCUIT", "Add timeouts and circuit breaker for a dependency", "Resilience", "6 min",
                            "A slow dependency can exhaust request threads because there is no timeout or circuit breaker.",
                            facts("Dependency", "Profile service", "Problem", "No timeout", "Control", "Timeout and circuit breaker"),
                            List.of("Set a short client timeout.", "Add a circuit breaker with fallback.", "Retest slow dependency and confirm the app recovers."),
                            "httpClient.timeout=2s\ncircuitBreaker.failureThreshold=5",
                            "Timeouts and circuit breaker protect the dependency call, fallback is safe, and retest confirms slow dependency no longer exhausts requests."))
    );

    private LabCatalog() {
    }

    public static List<LabModule> modules() {
        return MODULES;
    }

    static Optional<LabModule> findModule(String slug) {
        return MODULES.stream()
                .filter(module -> module.slug().equals(slug))
                .findFirst();
    }

    static Optional<Lab> findLab(LabModule module, String slug) {
        return module.labs().stream()
                .filter(lab -> lab.slug().equals(slug))
                .findFirst();
    }

    private static LabModule module(String slug, String code, String title, Lab... labs) {
        return new LabModule(slug, code, title, List.of(labs));
    }

    private static Lab lab(String slug,
                           String labType,
                           String title,
                           String badge,
                           String duration,
                           String scenario,
                           Map<String, String> facts,
                           List<String> steps,
                           String simulator,
                           String sampleEvidence) {
        return new Lab(slug, labType, title, badge, duration, scenario, facts, steps, simulator, sampleEvidence);
    }

    private static Map<String, String> facts(String key1, String value1, String key2, String value2, String key3, String value3) {
        Map<String, String> facts = new LinkedHashMap<>();
        facts.put(key1, value1);
        facts.put(key2, value2);
        facts.put(key3, value3);
        return facts;
    }

    public record LabModule(String slug, String code, String title, List<Lab> labs) {
    }

    public record Lab(String slug,
                      String labType,
                      String title,
                      String badge,
                      String duration,
                      String scenario,
                      Map<String, String> facts,
                      List<String> steps,
                      String simulator,
                      String sampleEvidence) {
    }
}
