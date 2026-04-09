const BASE_URL = "http://localhost:3000/api";

async function testAPI() {
  console.log("--- ĐANG BẮT ĐẦU TEST API ---");

  // 1. Test lấy danh sách Modules (Task 2)
  try {
    console.log("\n[TEST 1] GET /modules:");
    const resModules = await fetch(`${BASE_URL}/modules`);
    const modules = await resModules.json();

    if (resModules.ok) {
      console.log("✅ Thành công! Danh sách modules:");
      console.table(modules);
    } else {
      console.error("❌ Lỗi:", modules);
    }

    // 2. Test lấy Lessons của Module đầu tiên (Task 3)
    if (modules.length > 0) {
      const firstModuleId = modules[0].id;
      console.log(`\n[TEST 2] GET /lessons/${firstModuleId}:`);

      const resLessons = await fetch(`${BASE_URL}/lessons/${firstModuleId}`);
      const lessons = await resLessons.json();

      if (resLessons.ok) {
        console.log(
          `✅ Thành công! Các bài học của Module ID ${firstModuleId}:`,
        );
        console.table(lessons);
      } else {
        console.error("❌ Lỗi:", lessons);
      }
    } else {
      console.log(
        "\n⚠️ Bỏ qua Test 2 vì bảng Modules đang trống (Hãy insert dữ liệu trước).",
      );
    }
  } catch (error) {
    console.error(
      "❌ Không thể kết nối tới Server. Hãy chắc chắn bạn đã chạy node server.js!",
    );
    console.error("Chi tiết lỗi:", error.message);
  }
}

testAPI();
