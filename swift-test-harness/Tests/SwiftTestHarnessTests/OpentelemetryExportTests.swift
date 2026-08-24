import Testing
import Opentelemetry

@Suite("Opentelemetry Export Smoke Tests")
struct OpentelemetryExportTests {
    @Test("Swift module loads cleanly")
    func testSwiftModuleLoads() throws {
        #expect(true)
    }
}
