@RestController
public class HealthController {
    @Autowired
    private DataSource dataSource;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        try {
            dataSource.getConnection().close();
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("DB disconnected", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
