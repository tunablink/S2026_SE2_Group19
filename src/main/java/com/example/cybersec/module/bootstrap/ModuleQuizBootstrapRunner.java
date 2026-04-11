package com.example.cybersec.module.bootstrap;

import com.example.cybersec.module.entity.Module;
import com.example.cybersec.module.repository.ModuleRepository;
import com.example.cybersec.quiz.entity.QuizQuestion;
import com.example.cybersec.quiz.repository.QuizQuestionRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
public class ModuleQuizBootstrapRunner {

    @Bean
    ApplicationRunner seedModulesAndQuizzes(ModuleRepository moduleRepository,
                                            QuizQuestionRepository questionRepository) {
        return args -> seed(moduleRepository, questionRepository);
    }

    @Transactional
    void seed(ModuleRepository moduleRepository, QuizQuestionRepository questionRepository) {
        for (ModuleSeed seed : modules()) {
            Module module = moduleRepository.findByName(seed.slug())
                    .orElseGet(() -> moduleRepository.save(new Module(seed.title(), seed.slug(), seed.description())));
            seedQuestions(module, seed.answers(), questionRepository);
        }
    }

    private void seedQuestions(Module module, List<String> answers, QuizQuestionRepository questionRepository) {
        for (int i = 0; i < answers.size(); i++) {
            String questionKey = "Q" + (i + 1);
            String correctOption = answers.get(i);
            int sortOrder = i + 1;
            QuizQuestion question = questionRepository.findByModuleAndQuestionKey(module, questionKey)
                    .orElseGet(() -> new QuizQuestion(module, questionKey, correctOption, sortOrder));
            question.setCorrectOption(correctOption);
            question.setSortOrder(sortOrder);
            questionRepository.save(question);
        }
    }

    private List<ModuleSeed> modules() {
        return List.of(
                new ModuleSeed(
                        "A01: Broken Access Control",
                        "A01-broken-access-control",
                        "Authorization mistakes let users access data or actions they should not.",
                        List.of("B", "C", "B", "B", "C")
                ),
                new ModuleSeed(
                        "A02: Security Misconfiguration",
                        "A02-security-misconfiguration",
                        "Unsafe defaults, exposed services, weak CORS or headers, and debug mode in production.",
                        List.of("B", "B", "B", "C", "B")
                ),
                new ModuleSeed(
                        "A03: Software Supply Chain Failures",
                        "A03-software-supply-chain-failures",
                        "Dependency and build pipeline risks across packages, artifacts, and CI/CD.",
                        List.of("B", "B", "B", "C", "B")
                ),
                new ModuleSeed(
                        "A04: Cryptographic Failures",
                        "A04-cryptographic-failures",
                        "Weak protection of sensitive data in transit, at rest, and inside backups.",
                        List.of("B", "B", "B", "B", "B")
                ),
                new ModuleSeed(
                        "A05: Injection",
                        "A05-injection",
                        "Untrusted input reaching interpreters such as SQL, shell, LDAP, or template engines.",
                        List.of("B", "B", "B", "B", "D")
                ),
                new ModuleSeed(
                        "A06: Insecure Design",
                        "A06-insecure-design",
                        "Missing or weak design controls that allow abuse even when implementation is correct.",
                        List.of("B", "B", "A", "B", "B")
                ),
                new ModuleSeed(
                        "A07: Authentication Failures",
                        "A07-authentication-failures",
                        "Weak login, credential, session, and account recovery protections.",
                        List.of("B", "B", "B", "B", "B")
                ),
                new ModuleSeed(
                        "A08: Software or Data Integrity Failures",
                        "A08-software-or-data-integrity-failures",
                        "Missing integrity checks for updates, artifacts, pipelines, and serialized data.",
                        List.of("B", "B", "B", "B", "B")
                ),
                new ModuleSeed(
                        "A09: Security Logging and Monitoring Failures",
                        "A09-security-logging-and-monitoring-failures",
                        "Insufficient events, alerting, retention, and response readiness.",
                        List.of("B", "B", "B", "B", "B")
                ),
                new ModuleSeed(
                        "A10: Server-Side Request Forgery",
                        "A10-server-side-request-forgery",
                        "Unsafe server-side requests to user-controlled URLs and internal services.",
                        List.of("B", "B", "B", "B", "B")
                )
        );
    }

    private record ModuleSeed(String title, String slug, String description, List<String> answers) {
    }
}
