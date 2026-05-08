async function fetchAll() {
    const response = await fetch("/enrollments");
    const data = await response.json();
    renderResponse(data);
}

async function importCsv() {
    const csvText = document.getElementById("csvText").value.trim();
    const importBtn = document.getElementById("importBtn");

    if (!csvText) {
        alert("请输入CSV内容后再导入。");
        return;
    }

    importBtn.disabled = true;
    importBtn.textContent = "导入中...";

    try {
        const response = await fetch("/enrollments/import", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ csvText })
        });
        const data = await response.json();
        renderResponse(data);

        if (response.ok) {
            const count = (data.records || []).length;
            alert(`录入成功，当前共 ${count} 条选课记录。`);
        } else {
            alert("导入失败，请检查CSV格式后重试。");
        }
    } catch (error) {
        alert("导入失败：网络异常或服务未启动。");
    } finally {
        importBtn.disabled = false;
        importBtn.textContent = "导入并处理";
    }
}

async function searchRecords() {
    const keyword = document.getElementById("keyword").value.trim();
    const url = keyword ? `/enrollments/search?keyword=${encodeURIComponent(keyword)}` : "/enrollments";
    const response = await fetch(url);
    const data = await response.json();
    renderResponse(data);
}

function renderResponse(data) {
    const statusMsg = document.getElementById("statusMsg");
    const perfMsg = document.getElementById("perfMsg");
    const resultContainer = document.getElementById("resultContainer");
    statusMsg.textContent = data.message || "处理完成";
    perfMsg.textContent = `处理耗时：${data.elapsedMs ?? 0} ms，记录数：${(data.records || []).length}`;

    const grouped = data.groupedByType || {};
    const groupNames = ["公共课", "专业课", "选修课", "其他"];
    const html = groupNames.map(type => buildGroupHtml(type, grouped[type] || [])).join("");
    resultContainer.innerHTML = html;
}

function buildGroupHtml(groupName, records) {
    if (!records.length) {
        return `<div class="group"><h3>${groupName}</h3><p>暂无记录</p></div>`;
    }
    const rows = records.map(record => `
        <tr>
            <td>${escapeHtml(record.studentId)}</td>
            <td>${escapeHtml(record.courseId)}</td>
            <td>${escapeHtml(record.courseName)}</td>
            <td>${escapeHtml(record.courseType)}</td>
        </tr>
    `).join("");

    return `
    <div class="group">
        <h3>${groupName}</h3>
        <table>
            <thead>
                <tr>
                    <th>学生ID</th>
                    <th>课程ID</th>
                    <th>课程名称</th>
                    <th>课程类型</th>
                </tr>
            </thead>
            <tbody>
                ${rows}
            </tbody>
        </table>
    </div>`;
}

function escapeHtml(value) {
    const div = document.createElement("div");
    div.textContent = value ?? "";
    return div.innerHTML;
}

document.getElementById("importBtn").addEventListener("click", importCsv);
document.getElementById("searchBtn").addEventListener("click", searchRecords);
document.getElementById("resetBtn").addEventListener("click", () => {
    document.getElementById("keyword").value = "";
    fetchAll();
});

fetchAll();
