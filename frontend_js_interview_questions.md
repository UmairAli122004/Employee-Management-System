# Frontend JavaScript Interview Cross-Questions Guide

This document contains an absolute master-list of 50 potential cross-questions an interviewer might ask based on the frontend JavaScript implementation of the Employee Management System.

---

## Category 1: Asynchronous JavaScript & API (`api.js`)

### Q1: Why did you choose `async/await` over Promises (`.then()/.catch()`)?
**Answer:** 
`async/await` provides a more readable, synchronous-looking way to write asynchronous code. It flattens the "Promise chain" and avoids "callback hell."
* **Error Handling:** We can use standard `try/catch` blocks, making error handling consistent.
* **Debugging:** Stack traces are cleaner compared to anonymous Promise callbacks.

### Q2: What happens if you forget the `await` keyword before a `fetch` call?
**Answer:** 
The function will immediately return a pending `Promise` object instead of the actual data. Subsequent code trying to read properties (like `response.ok`) will fail because the Promise hasn't resolved.

### Q3: What does `credentials: 'same-origin'` do in the `fetch` options?
**Answer:** 
It tells the browser to send user credentials (like Cookies) only for **same-origin** requests. 
* If the API was on a different domain (CORS), `same-origin` would omit cookies.
* To send cookies cross-origin, we would need `credentials: 'include'`.

### Q4: Why use `cache: 'no-store'` in the `fetch` configuration?
**Answer:** 
It forces the browser to bypass the cache and fetch directly from the server. Stale data in an Employee Management System is dangerous for business logic.

### Q5: Why read `await response.text()` and then use `JSON.parse()` instead of `await response.json()`?
**Answer:** 
Directly calling `response.json()` throws an uncatchable `SyntaxError` if the response is empty (HTTP 204) or returns HTML errors. Reading as text allows us to check the content type safely first.

### Q6: Explain line 38 in `api.js`: `errorData.map(e => e.message).filter(Boolean).join('; ')`.
**Answer:** 
This parses Spring Boot validation errors:
* `.map(e => e.message)` extracts the error message from each validation failure.
* `.filter(Boolean)` removes any `null`, `undefined`, or empty strings.
* `.join('; ')` combines them into a single user-friendly string.

### Q7: Why did you use a `class ApiService` with `static` methods instead of just independent functions?
**Answer:** 
Using static methods groups related API logic inside a clean namespace (`ApiService.get()`, `ApiService.post()`) without requiring the creation of an object instance. It encapsulates the base URL and utility headers neatly.

---

## Category 2: Security & Storage (`auth.js`, `api.js`)

### Q8: Is storing user roles in `localStorage` secure?
**Answer:** 
No, `localStorage` is vulnerable to XSS. 
* **Why we do it:** To manage **UI state** (e.g., hiding buttons).
* **Security:** The *actual* authorization happens on the server. If a user alters their role in JS, the backend will still reject unauthorized requests.

### Q9: Why use `encodeURIComponent(empId)` in your API calls?
**Answer:** 
It escapes special characters (like `&`, `/`, spaces) to prevent **URI Injection** and broken URL routing.

### Q10: What happens if a user tampers with `userRole` in `localStorage` to access `admin.html`?
**Answer:** 
They might see the page layout, but `admin.js` calls `ensureAuth('ROLE_ADMIN')` which hits the server. If the server session is not an Admin session, they are booted back to the login page.

### Q11: You clear storage via `localStorage.clear()` on logout. What is the downside?
**Answer:** 
`localStorage.clear()` deletes **all** items in `localStorage` associated with the domain. If other apps on the same origin saved data there, it is destroyed. A more granular approach is using `localStorage.removeItem('userEmail')`.

---

## Category 3: DOM & State Management (`admin.js`, `employee.js`)

### Q12: Why wrap code in `document.addEventListener('DOMContentLoaded', ...)`?
**Answer:** 
To ensure the HTML DOM tree is fully built before we attempt to query elements like `document.getElementById()`, avoiding `null` reference errors.

### Q13: How does the client-side pagination work?
**Answer:** 
We track `currentPage`, `totalPages`, and `PAGE_SIZE`. Buttons trigger updates to `currentPage`, which refetches the paginated data from the backend.

### Q14: What is the risk of using `innerHTML` dynamically?
**Answer:** 
It opens the door to **XSS (Cross-Site Scripting)** if the API data is malicious. Using `textContent` or robust sanitization is preferred.

### Q15: Why use `parseInt()` and `parseFloat()` on input values?
**Answer:** 
Even if an `<input>` is `type="number"`, the `.value` property in JavaScript always returns a **string**. We must cast it before sending it to the backend.

### Q16: How do you handle the "click outside modal to close" functionality?
**Answer:** 
We listen to the click on the overlay. Using event target validation (`e.target === overlay`), we ensure the click wasn't inside the modal content before closing.

### Q17: Why did you set `document.body.style.overflow = 'hidden'` when the modal opens?
**Answer:** 
This prevents the main page content from scrolling up or down behind the modal while it is actively in the user's view.

---

## Category 4: Advanced JS & Tricky Concepts

### Q18: How does the `showConfirmToast` Promise work?
**Answer:** 
It wraps a DOM modal in a `Promise`. The `await` holds the execution until the user clicks "Yes" (calls `resolve(true)`) or "Cancel" (calls `resolve(false)`).

### Q19: Why check `if (visibleRows <= 1 && currentPage > 0)` upon deletion?
**Answer:** 
If we delete the last record on page 3, staying on page 3 shows a blank screen. This logic automatically moves the user to page 2.

### Q20: What is the difference between `== null` and `=== null` in `employee.js`?
**Answer:** 
* `value == null` checks if a value is **either** `null` or `undefined`.
* `value === null` checks strictly for `null` (and ignores `undefined`).

### Q21: What happens if `JSON.stringify()` encounters circular references?
**Answer:** 
It throws a `TypeError: Converting circular structure to JSON`. While our form data doesn't contain this, it is an important consideration when dumping large object states.

### Q22: Explain the difference between `let`, `const`, and `var`.
**Answer:** 
* `const`: Block-scoped, cannot be reassigned (used for constants like API limits).
* `let`: Block-scoped, mutable (used for state like `currentPage`).
* `var`: Function-scoped, hoisted, rarely recommended in ES6+.

### Q23: Explain the difference between `window.location.href` and `window.location.replace()`.
**Answer:** 
* `.href` sets the path and pushes it to the browser history (user can click "Back").
* `.replace()` overrides the current history entry.

### Q24: What is **Event Delegation** and how would it optimize table edits?
**Answer:** 
Instead of adding inline `onclick` handlers to 100 separate delete buttons, Event Delegation attaches **one** listener to the `<tbody>` and evaluates the `e.target` to see if a button was clicked.

---

## Category 5: JavaScript Architecture & UX

### Q25: Why use Vanilla JS instead of React or Angular?
**Answer:** 
For a standard CRUD app served directly via Spring Boot, Vanilla JS eliminates complex build steps, bundling, and keeps the frontend lightweight and fast to load.

### Q26: What is `e.preventDefault()` used for?
**Answer:** 
It stops the browser's default action. On forms, it prevents a full-page reload, allowing us to process the submission via AJAX/Fetch.

### Q27: What are the benefits of the `setButtonLoading` UX pattern?
**Answer:** 
It prevents **double-submission** (disabling the button) and provides visual feedback (spinners) for better perceived performance.

### Q28: How do you normalize non-array responses into arrays?
**Answer:** 
By executing `Array.isArray(response) ? response : (response ? [response] : [])`. This protects the logic from crashing if an API fails to provide lists.

### Q29: Explain `encodeURIComponent` vs `encodeURI`.
**Answer:** 
`encodeURIComponent` acts on standalone parameters (e.g. query strings) while `encodeURI` preserves formatting (like `http://`).

### Q30: Why is `setTimeout` used for UI cleanups?
**Answer:** 
To delay logic execution, such as allowing visual success indicators to show before routing changes destroy the DOM view.

---

## Category 6: Advanced Interviewer "Growth" Scenarios

### Q31: What is CORS, and how would you handle it if your backend moved to a different server?
**Answer:** 
Cross-Origin Resource Sharing. If the frontend and backend are separated, the browser blocks requests by default. You'd need to allow your frontend origin on the Spring Boot side via `@CrossOrigin` or a `WebMvcConfigurer`.

### Q32: How would you optimize a "Search-as-you-type" input?
**Answer:** 
By implementing **Debouncing**. This delays API requests until the user has paused typing for a set period (e.g., 300ms) avoiding API overload.

### Q33: What is CSRF protection, and how does your JS interact with it?
**Answer:** 
Cross-Site Request Forgery. Spring Security requires validating a CSRF token on mutate requests (POST/PUT/DELETE). The JS must retrieve the token from a meta tag or cookie and append it to headers.

### Q34: Do your event listeners form Closures?
**Answer:** 
Yes, nested callbacks retain access to outer scope data (like form element references) long after the parent execution has finished.

### Q35: How would you measure the UI's performance bottlenecks?
**Answer:** 
Using the browser **Performance Tab** to track frame rate drops during rapid DOM manipulation and **Lighthouse** to review caching strategies.

---

## Category 7: Strict Fundamentals & Deep Dives

### Q36: What are truthy and falsy values?
**Answer:** 
Falsy values evaluate to false in a boolean context (`false`, `0`, `""`, `null`, `undefined`, `NaN`). All other values are truthy.

### Q37: Why use `===` instead of `==` in data comparison?
**Answer:** 
`===` is strict equality (compares type and value). `==` performs implicit type coercion, which leads to bugs (e.g., `[] == false` is true).

### Q38: Explain the difference between `data.salary ?? '—'` and `data.salary || '—'`.
**Answer:** 
* `||` evaluates the right side if the left is **falsy** (fails if salary is explicitly `0`).
* `??` (Nullish Coalescing) evaluates the right side **only** if left is `null` or `undefined`.

### Q39: Why use arrow functions `() => {}` for DOM listeners?
**Answer:** 
Arrow functions do not bind their own `this`. They inherit it lexically from the parent scope, preventing common bugs where `this` unexpectedly refers to the button instead of the class.

### Q40: What are Template Literals?
**Answer:** 
Strings wrapped in backticks `` ` `` allowing multi-line text and embedded JS expressions using `${expression}`.

### Q41: What is the `e` parameter in event handlers?
**Answer:** 
The **Event Object** passed automatically by the browser. It contains metadata (target element, cursor location, keys pressed).

### Q42: Why throw custom `new Error()` over string messages?
**Answer:** 
Passing an `Error` object retains the stack trace, making troubleshooting much faster.

### Q43: How does JS free memory (Garbage Collection)?
**Answer:** 
Via the **Mark-and-Sweep** algorithm. The engine looks for unreachable objects starting from roots (e.g. `window`).

### Q44: Explain Microtasks vs Macrotasks execution order.
**Answer:** 
Microtasks (`Promise.then()`) run immediately after the current execution finishes, *before* macrotasks (`setTimeout`).

### Q45: How could you scale files via ES6 Modules (`import`/`export`)?
**Answer:** 
By refactoring standalone files to export specific functions and importing them securely via `<script type="module">`.

### Q46: What is a Polyfill?
**Answer:** 
Code used to provide modern functionality (`fetch`) on older browsers that do not natively support it.

### Q47: How would you write unit tests for `ApiService`?
**Answer:** 
Using Jest or Vitest to mock the native `window.fetch` and asserting status code branches.

### Q48: Explain Reflow and Repaint in DOM updates.
**Answer:** 
* **Reflow:** Recalculating element positions.
* **Repaint:** Redrawing the pixels. Minimizing layout adjustments avoids performance drops.

### Q49: What does `'use strict';` accomplish?
**Answer:** 
It enforces stricter parsing rules (e.g. bans uninitialized global variables).

### Q50: What is the Single Responsibility Principle in this codebase?
**Answer:** 
Separating authentication states into `auth.js` and communication states into `api.js`.
