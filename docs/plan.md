---
title: Academy Workshop Manager — суть
tags: [java, academy-manager]
status: done-core
---

# Academy Workshop Manager — суть

Ядро (шаги 0–6) готово и влито в `main`. Дальше: `Academy_manager_security.md`, потом рефакторинг.

## Модель

- `Student` владеет `ContactDetails` (композиция, оба `record`, неизменяемые).
- `Workshop` (abstract) хранит `List<Student>` — только ссылки (ассоциация). `unregister` убирает ссылку, студент остаётся в `Academy`.
- `Academy` — диспетчер: `HashMap` студентов, `LinkedHashMap` воркшопов, поиск по ID, делегирует `Workshop`.
- Правила регистрации живут только в `Workshop` (один источник правды).
- `buildReport()` возвращает `List<String>`; `ReportOutput` решает, куда писать (консоль / файл).

| Класс | Отвечает за |
|---|---|
| `Validation` | blank / null / positive → `IllegalArgumentException` |
| `ContactDetails`, `Student` | неизменяемые данные |
| `Workshop` → `ClassroomWorkshop`, `OnlineWorkshop` | вместимость, участники, `joiningInstructions()` |
| `Academy` | хранение, поиск, отчёты на стримах |
| `ReportOutput` → `ConsoleReportOutput`, `FileReportOutput` | вывод |
| `SimpleFiles`, `StudentFileLoader` | файловый ввод-вывод |
| `Main` | демо |

## Коллекции

| Где | Что | Почему |
|---|---|---|
| студенты | `HashMap<Integer, Student>` | поиск по ID |
| воркшопы | `LinkedHashMap<Integer, Workshop>` | ID + порядок создания |
| участники | `ArrayList<Student>` | порядок регистрации, `Iterator` |
| города | `TreeSet<String>` | уникальные, по алфавиту |
| счётчики | `TreeMap<String, Long>` | ключи по алфавиту |

## Правила, которые нельзя сломать

1. Невалидный аргумент (null, blank, id ≤ 0, неизвестный id, дубль id в академии) → `IllegalArgumentException`. Null тоже сюда: `Validation.requireNonNull`, не `Objects.requireNonNull`.
2. Состояние не позволяет (дубль регистрации, воркшоп полон) → `IllegalStateException`.
3. Порядок в `Workshop.register`: null → дубль → вместимость.
4. Удаление участника — через явный `Iterator` + `iterator.remove()`.
5. Поиск — case-insensitive substring (`Locale.ROOT`). Сортировка тоже case-insensitive: один компаратор `BY_NAME_THEN_ID` (`String.CASE_INSENSITIVE_ORDER`, тай-брейк по id), тест `SortingTest`. Менять только если спека явно требует иное.
6. Отчёт возвращает данные и сам не печатает.
7. Смена города = новый `Student` (по спеке); изменяемый контакт — только осознанный рефакторинг.

## Команды

```bash
mvn test                    # тесты
mvn -q compile exec:java    # демо (exec:java сам не компилирует)
```

Запускать из корня проекта: `data/students.txt` ищется относительно рабочей директории. Результат: `output/report.txt`.

## Критерий готовности

- `mvn test` зелёный; демо печатает корректный отчёт; после шага 6 есть `output/report.txt`.
- Unregister не удаляет студента из академии; дубль проверяется раньше вместимости.

## Открытые вопросы

- Пример поиска `arl → Chitrakalpa` в задании, скорее всего, опечатка: `arl` не подстрока. Не ломать поиск ради него; спросить преподавателя.
- Формулировку сортировки в спеке сверить с пунктом 5 (код и `SortingTest` сейчас case-insensitive).

## Рефакторинг (кандидаты)

- [ ] R1 `Main`: одна переменная `reportPath` вместо двух мест
- [ ] R2 `Academy.searchStudents`: `Validation.requireNonNull(text, "text")` вместо inline-проверки
- [ ] R3 `Main`: вынести сценарий в `private static void demo(Academy)`
- [ ] R4 тест в `FileIntegrationTest`: 5 колонок в строке файла → `IllegalArgumentException`
- [ ] по желанию: `AlreadyRegisteredException`, `WorkshopFullException` (наследуют `IllegalStateException`; тексты сообщений не менять — тесты проверяют `contains("already")`)
- [ ] не делать: `equals`/`hashCode` у record `Student` по ID (ломает контракт, дубли и так ловятся по `id()`); разнос по пакетам (`Validation` пришлось бы делать public)
- [ ] изменяемый контакт студента (вариант Б), обсудить с Борей
- [ ] консольное меню
- [ ] база данных — позже, через `StudentRepository`/`WorkshopRepository` в конструкторе `Academy`; правила остаются в `Workshop`; домен не импортирует `java.sql.*`; PK `(student_id, workshop_id)`, вместимость — в транзакции

## Шпаргалка для защиты

- Composition: `Student` владеет `ContactDetails`, отдельно тот не живёт.
- Association: `Workshop` держит ссылки; тест `unregister_keepsStudentInAcademy`.
- Коллекции: Map — поиск по ID; `LinkedHashMap` — порядок; `ArrayList` — порядок регистрации.
- Полиморфизм: `workshop.joiningInstructions()` без `instanceof`; `ReportOutput.write(...)` не знает, консоль это или файл.
- Исключения: Argument = запрос негоден; State = мир не позволяет.
- Iterator: безопасное удаление во время обхода.