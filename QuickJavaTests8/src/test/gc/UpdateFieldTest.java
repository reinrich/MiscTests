package test.gc;

/*

./sapjvm_8/bin/java
-XX:+ClientCompiler
-XX:+PrintLIR
-XX:-PrintIR
-XX:+TraceCompilerOracle
-XX:+PrintCompilation
-XX:CICompilerCount=1
-Xbatch
'-XX:CompileCommand=compileonlywithinlining,*::testMethod*'
'-XX:CompileCommand=dontinline,*::dontinline*'
-XX:-TieredCompilation
-cp
/u/w/d/jtests/QuickJavaTests/bin
test.gc.UpdateFieldTest

 */

public class UpdateFieldTest {

    public static class ObjectWrapper {
        public Object wrappedObj;
    }
    
    public static class ProduceGCLoad implements Runnable {
        
        public class TreeNode {
            public static final int NODE_BYTE_SIZE = 1000;
            public TreeNode left;
            public TreeNode right;
            public byte[] payload;
            
            public TreeNode(TreeNode left, TreeNode right) {
                this.left = left;
                this.right = right;
                payload = new byte[NODE_BYTE_SIZE];
            }
        }
        
        // builds the tree recursively
        public TreeNode buildTree(long size) {
            if (size > (TreeNode.NODE_BYTE_SIZE << 1)) {
                long half = size >> 2;
                TreeNode left = buildTree(half);
                TreeNode right = buildTree(half);
                return new TreeNode(left, right);
            }
            return null; // end recursion
        }

        @Override
        public void run() {
            TreeNode[] trees = new TreeNode[100];
            while (true) {
                for(int i = 0; i < trees.length; i++) {
                    trees[i] = buildTree(1 << 20);
                }
            }
            
        }
        
    }
    
    public static void main(String[] args) {
        Object obj = new Object();
        ObjectWrapper wrapper = new ObjectWrapper();
        Thread prodGCLoad = new Thread(new ProduceGCLoad());
        for(int i = 0; i < 20000; i++) {
            testMethod(wrapper, obj);
        }
        prodGCLoad.start();
        for(int i = 0; i < 1<<31; i++) {
            testMethod(wrapper, obj);
        }
    }

    public static void testMethod(ObjectWrapper wrapper, Object obj) {
        wrapper.wrappedObj = obj;
    }

}
