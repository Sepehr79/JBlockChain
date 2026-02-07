package org.sepehr.jblockchain.verification;

import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sepehr.jblockchain.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Merkle Tree implementation for Bitcoin-style transaction verification
 */
public class MerkleTree {

    private Node root;
    private List<Transaction> transactions;

    @RequiredArgsConstructor
    @Getter
    public static class TransactionProof {
        private final List<ProofElement> proofElement;
        private final String rootHash;
    }

    public static class ProofElement {
        public final String hash;
        public final boolean isLeft;

        public ProofElement(String hash, boolean isLeft) {
            this.hash = hash;
            this.isLeft = isLeft;
        }
    }

    /**
     * Node class representing each node in the Merkle Tree
     */
    static class Node {
        String hash;
        Node left;
        Node right;

        Node(String hash) {
            this.hash = hash;
        }

        Node(String hash, Node left, Node right) {
            this.hash = hash;
            this.left = left;
            this.right = right;
        }
    }

    public MerkleTree(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            throw new IllegalArgumentException("Transactions list cannot be null or empty");
        }
        this.transactions = new ArrayList<>(transactions);
        this.root = buildTree(transactions);
    }

    /**
     * Builds the Merkle Tree recursively
     */
    private Node buildTree(List<Transaction> data) {
        List<Node> nodes = new ArrayList<>();

        // Create leaf nodes from transaction data
        for (Transaction transaction : data) {
            nodes.add(new Node(hash(transaction)));
        }

        // Build tree bottom-up
        return buildTreeRecursive(nodes);
    }

    /**
     * Recursive method to build the tree from leaf nodes
     */
    private Node buildTreeRecursive(List<Node> nodes) {
        if (nodes.size() == 1) {
            return nodes.get(0);
        }

        List<Node> parentNodes = new ArrayList<>();

        // Process nodes in pairs
        for (int i = 0; i < nodes.size(); i += 2) {
            Node left = nodes.get(i);
            Node right;

            // If odd number of nodes, duplicate the last one (Bitcoin convention)
            if (i + 1 < nodes.size()) {
                right = nodes.get(i + 1);
            } else {
                right = nodes.get(i);
            }

            // Create parent node with combined hash
            String combinedHash = hash(left.hash + right.hash);
            parentNodes.add(new Node(combinedHash, left, right));
        }

        return buildTreeRecursive(parentNodes);
    }

    /**
     * SHA-256 hashing function
     */
    private static String hash(Transaction transaction) {
        byte[] hashBytes = transaction.getHash();
        // Convert to hexadecimal string
        return getString(hashBytes);
    }

    private static String hash(String string) {
        byte[] hashBytes = Hashing.sha256().hashBytes(string.getBytes()).asBytes();

        // Convert to hexadecimal string
        return getString(hashBytes);
    }

    private static String getString(byte[] hashBytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Get the Merkle Root
     */
    public String getMerkleRoot() {
        return root != null ? root.hash : null;
    }

    /**
     * Verify if a transaction exists in the tree
     * Returns the proof path (list of hashes needed for verification)
     */
    public List<ProofElement> getProof(Transaction transaction) {
        List<ProofElement> proof = new ArrayList<>();
        int index = transactions.indexOf(transaction);

        if (index == -1) {
            return null; // Transaction not found
        }

        getProofRecursive(root, hash(transaction), proof, index, transactions.size());
        return proof;
    }

    /**
     * Recursive method to build the proof path
     */
    private boolean getProofRecursive(Node node, String targetHash, List<ProofElement> proof,
                                      int index, int totalLeaves) {
        if (node == null) {
            return false;
        }

        // Leaf node
        if (node.left == null && node.right == null) {
            return node.hash.equals(targetHash);
        }

        // Determine which subtree to search
        int leftSize = getLeftSubtreeSize(totalLeaves);

        if (index < leftSize) {
            // Target is in left subtree
            if (getProofRecursive(node.left, targetHash, proof, index, leftSize)) {
                if (node.right != null) {
                    proof.add(new ProofElement(node.right.hash, false)); // sibling is on RIGHT
                }
                return true;
            }
        } else {
            // Target is in right subtree
            if (getProofRecursive(node.right, targetHash, proof, index - leftSize,
                    totalLeaves - leftSize)) {
                if (node.left != null) {
                    proof.add(new ProofElement(node.left.hash, true)); // sibling is on LEFT
                }
                return true;
            }
        }

        return false;
    }

    /**
     * Calculate the size of the left subtree
     */
    private int getLeftSubtreeSize(int totalSize) {
        if (totalSize == 1) {
            return 1;
        }
        int powerOfTwo = 1;
        while (powerOfTwo * 2 < totalSize) {
            powerOfTwo *= 2;
        }
        return powerOfTwo;
    }

    /**
     * Verify a transaction using its proof
     */
    public static boolean verifyTransaction(Transaction transaction, List<ProofElement> proof, String merkleRoot) {
        String currentHash = hash(transaction);

        for (ProofElement element : proof) {
            if (element.isLeft) {
                // Sibling goes on the left
                currentHash = hash(element.hash + currentHash);
            } else {
                // Sibling goes on the right
                currentHash = hash(currentHash + element.hash);
            }
        }

        return currentHash.equals(merkleRoot);
    }

    /**
     * Print the tree structure (for debugging)
     */
    public void printTree() {
        printTreeRecursive(root, "", true);
    }

    private void printTreeRecursive(Node node, String prefix, boolean isTail) {
        if (node != null) {
            System.out.println(prefix + (isTail ? "└── " : "├── ") +
                    node.hash.substring(0, 8) + "...");

            if (node.left != null || node.right != null) {
                if (node.left != null) {
                    printTreeRecursive(node.left, prefix + (isTail ? "    " : "│   "),
                            node.right == null);
                }
                if (node.right != null) {
                    printTreeRecursive(node.right, prefix + (isTail ? "    " : "│   "), true);
                }
            }
        }
    }
}