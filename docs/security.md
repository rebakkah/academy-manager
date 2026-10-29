---
title: Academy Manager — security (минимум)
tags: [java, academy-manager, security]
---

# Security — минимум

Цель: не допустить глупых утечек и подделки вывода. Без enterprise. Каждый шаг: своя ветка, `mvn test` зелёный, один смысл.

## 0. Перед началом

1. Открыть вкладку Pull requests: если висят открытые PR — влить или закрыть.
2. `main` не защищена, grokbot коммитил прямо в неё → включить защиту (только через PR, зелёный `mvn test`).
3. Слитые ветки `step-2-workshops … step-6-files` удалить.
4. В merge-коммитах PR #3–#5 author = личная почта (публичная история). Историю не переписывать: merge-коммиты подписаны GitHub, rewrite сломает подписи и все SHA. Решение записать в `SECURITY.md`.
5. `Validation` package-private → security-тесты кладём в `src/test/java/academy/`.

## 1. PR 1 — гигиена репозитория (код не меняется)

Ветка: `git checkout -b chore/repo-hygiene`

**Email автора.** Проверить:

```bash
git log --all --format='%an <%ae> | %cn <%ce>' | sort -u
```

Личная почта → GitHub Settings → Emails: «Keep my email addresses private» + «Block command line pushes that expose my email». Локально:

```bash
git config user.email "ID+rebakkah@users.noreply.github.com"   # точный ID — в тех же настройках
```

Историю не переписывать (меняет хэши, ломает PR, копии не удалит), если почта не критична.

**Скан истории:**

```bash
git grep -I -n -E "[A-Za-z0-9._-]+@[A-Za-z0-9.-]+\.[a-z]{2,}" $(git rev-list --all)
git grep -I -n -i -E "password|secret|token|api[_-]?key" $(git rev-list --all)
```

Допустим только `@example.com`. Данные в тестах и демо — только вымышленные. Утёкший секрет: сначала отозвать и заменить, потом чистить историю.

**`.gitignore`** — добавить:

```gitignore
.env
*.pem
*.key
*.log
hs_err_pid*
.DS_Store
Thumbs.db
data/*.local.txt
.aider*
!.aider.model.settings.yml
```

(Уже есть: `target/`, `.idea/`, `*.iml`, `.vscode/`, `output/`. `.aider*` — история чатов aider может содержать код и пути.)

**Зависимости.** `mvn versions:display-dependency-updates versions:display-plugin-updates`; обновлять отдельным коммитом + `mvn test`. Файл `.github/dependabot.yml`:

```yaml
version: 2
updates:
  - package-ecosystem: "maven"
    directory: "/"
    schedule:
      interval: "weekly"
```

**GitHub Settings → Code security:** Dependabot alerts + security updates, Secret scanning + push protection, CodeQL (по желанию).

## 2. PR 2 — запрет управляющих символов

Зачем: `Ann\nEvil` превращает одну запись отчёта в две (подделка вывода). Правка в одном месте — `Validation.requireText`:

```java
static String requireText(String value, String field) {
    if (value == null || value.isBlank()) {
        throw new IllegalArgumentException(field + " must not be blank");
    }
    String trimmed = value.trim();
    // "\u0000".isBlank() == false, но trim() его снимает: без этой проверки
    // пустое имя прошло бы как валидное
    if (trimmed.isEmpty()) {
        throw new IllegalArgumentException(field + " must not be blank");
    }
    if (trimmed.chars().anyMatch(Character::isISOControl)) {
        throw new IllegalArgumentException(field + " must not contain control characters");
    }
    return trimmed;
}
```

Проверка после `trim()`: `"Ann\n"` → `"Ann"` (ок), `"Ann\nEvil"` → отказ. Сначала тесты (красные), потом код:

```java
class ValidationSecurityTest {
    @ParameterizedTest
    @ValueSource(strings = {"A\nB", "A\rB", "A\tB", "A\u0000B"})
    void requireText_controlCharInside_isRejected(String v) {
        assertThrows(IllegalArgumentException.class, () -> Validation.requireText(v, "name"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"\u0000", "\u001F", "\u0000\u0000"})
    void requireText_onlyControlChars_isRejected(String v) {
        assertThrows(IllegalArgumentException.class, () -> Validation.requireText(v, "name"));
    }

    @Test
    void requireText_controlCharAtEdge_isTrimmed() {
        assertEquals("Ann", Validation.requireText(" Ann\n", "name"));
    }

    @Test
    void student_nameWithNewline_isRejected() {
        ContactDetails c = new ContactDetails("ann@example.com", "Brussels");
        assertThrows(IllegalArgumentException.class, () -> new Student(1, "Ann\nEvil", c));
    }

    @Test
    void contactDetails_cityWithNewline_isRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new ContactDetails("ann@example.com", "Brussels\nGhent"));
    }
}
```

Нужен `junit-jupiter-params`: артефакт `junit-jupiter` в `pom.xml` его уже включает. Если есть `StudentFileLoader` — добавить тест, что строка `1;Ann\tEvil;...` отклоняется. По желанию — по тесту на `title`, `room`, `meetingUrl`.

## 3. Не делать сейчас

- regex для email (достаточно «не пусто, без управляющих символов»);
- Spring Security, JWT, шифрование, роли, аудит;
- лимиты длины, пока нет недоверенного входа.

Когда появится ввод извне:

- путь от пользователя → containment-проверка:

```java
Path root = Path.of("data").toAbsolutePath().normalize();
Path requested = root.resolve(userInput).normalize();
if (!requested.startsWith(root)) throw new IllegalArgumentException("path escapes data dir");
// симлинки: toRealPath()
```

- файл → лимит размера и валидация строк;
- база → только `PreparedStatement`, никакой склейки SQL.

## 4. SECURITY.md (в корень, своими словами)

```markdown
# Security notes
Educational project. All sample data is synthetic (example.com).

## Handled
- Text fields reject blank values and control characters.
- Ids and capacities must be positive; invalid input throws IllegalArgumentException.
- Student and ContactDetails are immutable.
- Dependencies are watched by Dependabot.

## Not handled
- No authentication (local program).
- File input / user paths: add size limit and path containment check.
- Database: parameterized queries only.
```

## 5. Рефакторинг (после security)

Правила: один небольшой рефакторинг — один коммит; тесты не меняются и зелёные; поведение и чистка не смешиваются. Кандидаты R1–R4 — в конце `Academy_Workshop_Manager.md`.

Работа в VSCodium + aider (локальная модель):

```bash
export OLLAMA_API_BASE=http://127.0.0.1:11434
aider --model ollama_chat/qwen3.6:35b-a3b --no-auto-commits
```

- `--no-auto-commits`: коммиты делаешь сам, по одному смыслу.
- Файлы добавлять по одному: `/add src/main/java/academy/Academy.java`.
- У Ollama маленький контекст по умолчанию — поднять `num_ctx` (в `.aider.model.settings.yml`), иначе aider «забывает» файлы.
- После каждой правки: `mvn test`, смотреть `git diff`, потом коммит.

## Готово, когда

- [ ] `git log` — только noreply-адрес (или осознанно оставлено)
- [ ] в истории нет реальных email, секретов, личных путей
- [ ] `.gitignore` дополнен, `dependabot.yml` добавлен, alerts и secret scanning включены, `main` защищён
- [ ] `ValidationSecurityTest` и `mvn test` зелёные
- [ ] `SECURITY.md` написан
- [ ] можешь назвать границу доверия (что приходит снаружи) и сознательно оставленные риски