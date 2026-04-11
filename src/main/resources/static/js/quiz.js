(function () {
  const selectedByQuestion = {};

  document.addEventListener('DOMContentLoaded', function () {
    const quiz = document.querySelector('.AQuiz');
    if (!quiz) {
      return;
    }

    const questions = Array.from(document.querySelectorAll('.AQuiz__q'));
    questions.forEach(function (question, index) {
      const questionKey = 'Q' + (index + 1);
      question.dataset.questionKey = questionKey;
      removeShowAnswerButton(question);
      removeHint(question);
      question.querySelectorAll('.AQuiz__opt').forEach(function (option) {
        const optionKey = getOptionKey(option);
        option.dataset.optionKey = optionKey;
        option.setAttribute('role', 'radio');
        option.setAttribute('aria-checked', 'false');
        option.setAttribute('tabindex', '0');
        option.addEventListener('click', function () {
          selectOption(question, option);
        });
        option.addEventListener('keydown', function (event) {
          if (event.key === 'Enter' || event.key === ' ') {
            event.preventDefault();
            selectOption(question, option);
          }
        });
      });
    });

    buildSubmitArea(quiz, questions.length);
  });

  function selectOption(question, option) {
    const questionKey = question.dataset.questionKey;
    selectedByQuestion[questionKey] = option.dataset.optionKey;
    const correctOption = getCorrectOptionFromAnswer(question);
    question.querySelectorAll('.AQuiz__opt').forEach(function (item) {
      const selected = item === option;
      item.classList.remove('AQuiz__opt--correct', 'AQuiz__opt--wrong');
      item.classList.toggle('AQuiz__opt--selected', selected);
      item.setAttribute('aria-checked', selected ? 'true' : 'false');
      if (correctOption && item.dataset.optionKey === correctOption) {
        item.classList.add('AQuiz__opt--correct');
      }
      if (correctOption && selected && item.dataset.optionKey !== correctOption) {
        item.classList.add('AQuiz__opt--wrong');
      }
    });
    revealAnswer(question);
  }

  function getOptionKey(option) {
    const key = option.querySelector('.AQuiz__optKey');
    return (key ? key.textContent : '').replace('.', '').trim().toUpperCase();
  }

  function removeShowAnswerButton(question) {
    const button = question.querySelector('.AQuiz__answerBtn');
    if (button) {
      button.remove();
    }
  }

  function removeHint(question) {
    const hint = question.querySelector('.AQuiz__hint');
    if (hint) {
      hint.remove();
    }
  }

  function getCorrectOptionFromAnswer(question) {
    const line = question.querySelector('.AQuiz__answerLine');
    const text = line ? line.textContent : '';
    const match = text.match(/Correct:\s*([A-Z])/i);
    return match ? match[1].toUpperCase() : '';
  }

  function revealAnswer(question) {
    const answer = question.querySelector('.AQuiz__answer');
    if (answer) {
      answer.open = true;
    }
  }

  function buildSubmitArea(quiz, totalQuestions) {
    const footer = quiz.querySelector('.AQuiz__footer') || quiz;
    const panel = document.createElement('div');
    panel.className = 'AQuiz__submitPanel';

    const status = document.createElement('p');
    status.className = 'AQuiz__submitStatus';
    status.textContent = 'Choose one answer for every question, then submit.';

    const button = document.createElement('button');
    button.className = 'AQuiz__submit';
    button.type = 'button';
    button.textContent = 'Submit quiz';
    button.addEventListener('click', function () {
      submitQuiz(button, retryButton, status, totalQuestions);
    });

    const retryButton = document.createElement('button');
    retryButton.className = 'AQuiz__retry';
    retryButton.type = 'button';
    retryButton.textContent = 'Retry quiz';
    retryButton.hidden = true;
    retryButton.addEventListener('click', function () {
      resetQuiz(button, retryButton, status);
    });

    panel.appendChild(status);
    panel.appendChild(button);
    panel.appendChild(retryButton);
    footer.prepend(panel);
  }

  async function submitQuiz(button, retryButton, status, totalQuestions) {
    const missing = [];
    for (let i = 1; i <= totalQuestions; i++) {
      if (!selectedByQuestion['Q' + i]) {
        missing.push(i);
      }
    }
    if (missing.length > 0) {
      status.textContent = 'Please answer question ' + missing.join(', ') + '.';
      status.className = 'AQuiz__submitStatus AQuiz__submitStatus--error';
      return;
    }

    button.disabled = true;
    status.textContent = 'Submitting...';
    status.className = 'AQuiz__submitStatus';

    try {
      const response = await fetch('/api/quizzes/submit', {
        method: 'POST',
        credentials: 'same-origin',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          moduleSlug: getModuleSlug(),
          answers: selectedByQuestion
        })
      });

      if (!response.ok) {
        throw new Error('HTTP ' + response.status);
      }

      const result = await response.json();
      applyResult(result);
      status.textContent =
        result.message + ' Score: ' + result.score + '% (' +
        result.correctCount + '/' + result.totalQuestions + ').';
      status.className = 'AQuiz__submitStatus ' +
        (result.passed ? 'AQuiz__submitStatus--success' : 'AQuiz__submitStatus--error');
      if (!result.passed) {
        button.hidden = true;
        retryButton.hidden = false;
      }
    } catch (error) {
      status.textContent = 'Could not submit quiz. Please sign in and try again.';
      status.className = 'AQuiz__submitStatus AQuiz__submitStatus--error';
      button.disabled = false;
    }
  }

  function getModuleSlug() {
    const parts = window.location.pathname.split('/');
    return decodeURIComponent(parts[parts.length - 1] || '');
  }

  function applyResult(result) {
    (result.answers || []).forEach(function (answer) {
      const question = document.querySelector(
        '.AQuiz__q[data-question-key="' + answer.questionKey + '"]'
      );
      if (!question) {
        return;
      }
      question.querySelectorAll('.AQuiz__opt').forEach(function (option) {
        option.classList.remove('AQuiz__opt--correct', 'AQuiz__opt--wrong');
        const key = option.dataset.optionKey;
        if (key === answer.correctOption) {
          option.classList.add('AQuiz__opt--correct');
        }
        if (key === answer.selectedOption && !answer.correct) {
          option.classList.add('AQuiz__opt--wrong');
        }
      });
    });
  }

  function resetQuiz(button, retryButton, status) {
    Object.keys(selectedByQuestion).forEach(function (key) {
      delete selectedByQuestion[key];
    });

    document.querySelectorAll('.AQuiz__opt').forEach(function (option) {
      option.classList.remove('AQuiz__opt--selected', 'AQuiz__opt--correct', 'AQuiz__opt--wrong');
      option.setAttribute('aria-checked', 'false');
    });

    document.querySelectorAll('.AQuiz__answer').forEach(function (answer) {
      answer.open = false;
    });

    status.textContent = 'Choose one answer for every question, then submit.';
    status.className = 'AQuiz__submitStatus';
    button.disabled = false;
    button.hidden = false;
    retryButton.hidden = true;
  }
})();
