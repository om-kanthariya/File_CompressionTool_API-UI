package com.backend.API_fileCompression.services;

import com.backend.API_fileCompression.utility.HuffmanNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

@Service
public class HuffmanService {

    private Map<Character, String> huffmanCodes = new HashMap<>();
    private HuffmanNode root;

    public String compress(String input) {
        Map<Character, Integer> frequencyMap = buildFrequencyMap(input);
        root = buildHuffmanTree(frequencyMap);
        buildHuffmanCodes(root, "");
        return encode(input);
    }

    private Map<Character, Integer> buildFrequencyMap(String input) {
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char ch : input.toCharArray()) {
            freqMap.put(ch, freqMap.getOrDefault(ch, 0) + 1);
        }
        return freqMap;
    }

    private HuffmanNode buildHuffmanTree(Map<Character, Integer> freqMap) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (var entry : freqMap.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            HuffmanNode merged = new HuffmanNode('\0', left.frequency + right.frequency);
            merged.left = left;
            merged.right = right;
            pq.add(merged);
        }

        return pq.poll();
    }

    private void buildHuffmanCodes(HuffmanNode node, String code) {
        if (node == null) return;

        if (node.isLeaf()) {
            huffmanCodes.put(node.character, code);
        }
        buildHuffmanCodes(node.left, code + "0");
        buildHuffmanCodes(node.right, code + "1");
    }

    private String encode(String input) {
        return input.chars()
                .mapToObj(c -> huffmanCodes.get((char) c))
                .collect(Collectors.joining());
    }

    public Map<Character, String> getCodes() {
        return huffmanCodes;
    }

    public String decompress(String encodedBinary) {
        StringBuilder decodedText = new StringBuilder();
        HuffmanNode current = root;

        for (char bit : encodedBinary.toCharArray()) {
            current = (bit == '0') ? current.left : current.right;

            if (current.isLeaf()) {
                decodedText.append(current.character);
                current = root;
            }
        }

        return decodedText.toString();
    }


    public void saveHuffmanCodes(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(filePath), huffmanCodes);
    }

    public byte[] compressToBytes(String input) {
        compress(input); // builds tree and codes
        String binary = encode(input);

        // Convert binary string to byte array
        int byteLength = (binary.length() + 7) / 8;
        byte[] byteArray = new byte[byteLength];

        for (int i = 0; i < binary.length(); i++) {
            if (binary.charAt(i) == '1') {
                byteArray[i / 8] |= 1 << (7 - (i % 8));
            }
        }

        return byteArray;
    }



    public Map<String, Character> loadHuffmanCodes(String jsonFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> codeMap = mapper.readValue(new File(jsonFilePath), new TypeReference<>() {});
        Map<String, Character> reverseMap = new HashMap<>();

        for (Map.Entry<String, String> entry : codeMap.entrySet()) {
            reverseMap.put(entry.getValue(), entry.getKey().charAt(0));
        }

        return reverseMap;
    }

    public String byteArrayToBinaryString(byte[] byteArray, int originalBitLength) {
        StringBuilder binaryString = new StringBuilder();

        for (byte b : byteArray) {
            binaryString.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }

        return binaryString.substring(0, originalBitLength); // truncate padding
    }

    public String decodeWithCodeMap(String binary, Map<String, Character> reverseCodeMap) {
        StringBuilder decoded = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        for (char bit : binary.toCharArray()) {
            temp.append(bit);
            if (reverseCodeMap.containsKey(temp.toString())) {
                decoded.append(reverseCodeMap.get(temp.toString()));
                temp.setLength(0);
            }
        }

        return decoded.toString();
    }


}