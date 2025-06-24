package com.backend.API_fileCompression.controller;

import com.backend.API_fileCompression.services.HuffmanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:4200")
public class FileController {

    @Autowired
    private HuffmanService huffmanService;

    @PostMapping("/compress")
    public ResponseEntity<byte[]> compressFile(@RequestParam("file") MultipartFile file) throws IOException {
        byte[] compressedData;

        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteStream)) {

            gzipOutputStream.write(file.getBytes());
            gzipOutputStream.close();
            compressedData = byteStream.toByteArray();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", file.getOriginalFilename() + ".gz");

        return new ResponseEntity<>(compressedData, headers, HttpStatus.OK);
    }



    @PostMapping("/compress-new")
    public ResponseEntity<?> compressFileNew(@RequestParam("file") MultipartFile file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            String original = content.toString();
            String compressed = huffmanService.compress(original);
            var codes = huffmanService.getCodes();

            return ResponseEntity.ok(Map.of(
                    "originalSize", original.length(),
                    "compressedSize", compressed.length(),
                    "compressionRatio", (double) compressed.length() / original.length(),
                    "huffmanCodes", codes,
                    "compressedBinary", compressed
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to compress file: " + e.getMessage());
        }
    }


    @PostMapping("/decompress")
    public ResponseEntity<?> decompress(
            @RequestParam("compressedBinary") String binary
    ) {
        try {
            String decompressed = huffmanService.decompress(binary);

            return ResponseEntity.ok(Map.of(
                    "decompressedText", decompressed
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to decompress: " + e.getMessage());
        }
    }


    @PostMapping("/compress-to-file")
    public ResponseEntity<?> compressToFile(@RequestParam("file") MultipartFile file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            String original = content.toString();
            String compressed = huffmanService.compress(original);

            Path outputPath = Path.of("compressed_output.huff");
            Files.writeString(outputPath, compressed);

            return ResponseEntity.ok(Map.of(
                    "message", "File compressed and saved to disk",
                    "outputPath", outputPath.toAbsolutePath().toString()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to compress and save: " + e.getMessage());
        }
    }

    @GetMapping("/download-compressed")
    public ResponseEntity<byte[]> downloadCompressedFile() {
        try {
            String original = Files.readString(Path.of("sample.txt"));
            byte[] compressedBytes = huffmanService.compressToBytes(original);

            // Save Huffman codes to a file for decoding later
            huffmanService.saveHuffmanCodes("huffman_codes.json");

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=compressed.huff")
                    .body(compressedBytes);

        } catch (IOException e) {
            return ResponseEntity.status(500).body(("Error: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/decompress-file")
    public ResponseEntity<?> decompressFromFile() {
        try {
            Path filePath = Path.of("compressed.huff");
            byte[] byteData = Files.readAllBytes(filePath);

            int originalBitLength = 328; // You should store this during compression
            HuffmanService service = new HuffmanService();

            // Load reverse Huffman map
            Map<String, Character> reverseMap = service.loadHuffmanCodes("huffman_codes.json");

            // Convert binary from byte array
            String binary = service.byteArrayToBinaryString(byteData, originalBitLength);

            // Decode
            String originalText = service.decodeWithCodeMap(binary, reverseMap);

            return ResponseEntity.ok(Map.of("decompressedText", originalText));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during decompression: " + e.getMessage());
        }
    }


}
