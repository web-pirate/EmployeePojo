const employeeTableBody = document.querySelector("#employee-table tbody");
if (employeeTableBody) {
  fetch("/employee/api/employee")
    .then((res) => res.json())
    .then((employees) => {
      employeeTableBody.innerHTML = employees
        .map((e) => `<tr><td>${e.id}</td><td>${e.name}</td><td>${e.age}</td></tr>`)
        .join("");
    });
}
