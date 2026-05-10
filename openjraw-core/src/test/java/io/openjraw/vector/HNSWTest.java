package io.openjraw.vector;

import java.util.*;

import org.junit.jupiter.api.Test;

/**
 * HNSW (Hierarchical Navigable Small World) 구현
 * 논문: Malkov & Yashunin 2018
 *
 * 핵심: 층별 그래프를 만들어 O(log n) 근사 최근접 이웃 검색
 */
public class HNSWTest {

    // ─────────────────────────────────────────
    // 파라미터 (논문 권장값 기준)
    // ─────────────────────────────────────────
    private final int M;               // 레이어당 최대 연결 수 (논문: 5~48)
    private final int Mmax0;           // Layer 0 최대 연결 수 (논문: 2*M)
    private final int efConstruction;  // 인덱스 구축 시 후보 리스트 크기
    private final double mL;           // 층 생성 확률 조절 (논문: 1/ln(M))

    // ─────────────────────────────────────────
    // 내부 상태
    // ─────────────────────────────────────────
    private final List<float[]> vectors = new ArrayList<>();          // 저장된 벡터들
    private final List<List<List<Integer>>> graph = new ArrayList<>(); // graph[node][layer] = 이웃 목록
    private int entryPoint = -1;   // 검색 시작점 (최상위 층의 노드)
    private int maxLayer = -1;     // 현재 최대 층
    private final Random random = new Random(42);

    public HNSWTest(int M, int efConstruction) {
        this.M = M;
        this.Mmax0 = 2 * M;
        this.efConstruction = efConstruction;
        this.mL = 1.0 / Math.log(M); // 논문 Algorithm 1, line 4
    }

    // ─────────────────────────────────────────
    // INSERT (논문 Algorithm 1)
    // ─────────────────────────────────────────
    public void insert(float[] vector) {
        int id = vectors.size();
        vectors.add(vector);

        // 이 노드가 속할 최대 층 결정 (기하분포)
        int nodeLayer = (int) (-Math.log(random.nextDouble()) * mL); // Algorithm 1, line 4

        // graph 초기화: nodeLayer+1 개의 층
        List<List<Integer>> nodeLayers = new ArrayList<>();
        for (int i = 0; i <= nodeLayer; i++) {
            nodeLayers.add(new ArrayList<>());
        }
        graph.add(nodeLayers);

        // 첫 번째 노드
        if (entryPoint == -1) {
            entryPoint = id;
            maxLayer = nodeLayer;
            return;
        }

        int ep = entryPoint;

        // Phase 1: 상위 층에서 단순 그리디 탐색으로 가까운 진입점 찾기
        for (int lc = maxLayer; lc > nodeLayer; lc--) {
            List<int[]> w = searchLayer(vector, ep, 1, lc);
            ep = w.get(0)[0]; // 가장 가까운 노드
        }

        // Phase 2: nodeLayer ~ 0 층에서 이웃 연결
        for (int lc = Math.min(maxLayer, nodeLayer); lc >= 0; lc--) {
            List<int[]> w = searchLayer(vector, ep, efConstruction, lc);

            // 이 층에서 연결할 이웃 선택
            int maxConn = (lc == 0) ? Mmax0 : M;
            List<Integer> neighbors = selectNeighborsHeuristic(id, w, maxConn);

            // 양방향 연결
            for (int neighbor : neighbors) {
                graph.get(id).get(lc).add(neighbor);
                graph.get(neighbor).get(lc).add(id);

                // 이웃의 연결 수가 초과되면 pruning
                List<Integer> neighborConns = graph.get(neighbor).get(lc);
                if (neighborConns.size() > maxConn) {
                    pruneConnections(neighbor, lc, maxConn);
                }
            }

            // 다음 층 진입점 = 현재 층에서 가장 가까운 노드
            ep = w.get(0)[0];
        }

        // 새 노드가 더 높은 층이면 진입점 갱신
        if (nodeLayer > maxLayer) {
            maxLayer = nodeLayer;
            entryPoint = id;
        }
    }

    // ─────────────────────────────────────────
    // SEARCH-LAYER (논문 Algorithm 2)
    // ─────────────────────────────────────────
    // 반환: int[]{nodeId, distanceBits} 리스트 (거리 오름차순)
    private List<int[]> searchLayer(float[] query, int ep, int ef, int layer) {
        Set<Integer> visited = new HashSet<>();
        visited.add(ep);

        // candidates: 거리 오름차순 (가까운 것 먼저 꺼냄)
        PriorityQueue<int[]> candidates = new PriorityQueue<>(
                Comparator.comparingDouble(a -> Float.intBitsToFloat(a[1]))
        );
        // results: 거리 내림차순 (가장 먼 것 먼저 꺼냄 → 최대힙으로 ef 유지)
        PriorityQueue<int[]> results = new PriorityQueue<>(
                (a, b) -> Float.compare(Float.intBitsToFloat(b[1]), Float.intBitsToFloat(a[1]))
        );

        float epDist = distance(query, vectors.get(ep));
        int epDistBits = Float.floatToIntBits(epDist);
        candidates.add(new int[]{ep, epDistBits});
        results.add(new int[]{ep, epDistBits});

        while (!candidates.isEmpty()) {
            int[] current = candidates.poll();
            int cId = current[0];
            float cDist = Float.intBitsToFloat(current[1]);

            // 결과 중 가장 먼 거리보다 현재가 더 멀면 종료 (Algorithm 2, line 7-8)
            float furthestDist = Float.intBitsToFloat(results.peek()[1]);
            if (cDist > furthestDist) break;

            // 이웃 탐색
            List<Integer> neighbors = getNeighbors(cId, layer);
            for (int neighbor : neighbors) {
                if (visited.contains(neighbor)) continue;
                visited.add(neighbor);

                float nDist = distance(query, vectors.get(neighbor));
                furthestDist = Float.intBitsToFloat(results.peek()[1]);

                if (nDist < furthestDist || results.size() < ef) {
                    int nDistBits = Float.floatToIntBits(nDist);
                    candidates.add(new int[]{neighbor, nDistBits});
                    results.add(new int[]{neighbor, nDistBits});
                    if (results.size() > ef) {
                        results.poll(); // 가장 먼 것 제거
                    }
                }
            }
        }

        // 거리 오름차순으로 변환하여 반환
        List<int[]> sorted = new ArrayList<>(results);
        sorted.sort(Comparator.comparingDouble(a -> Float.intBitsToFloat(a[1])));
        return sorted;
    }

    // ─────────────────────────────────────────
    // SELECT-NEIGHBORS-HEURISTIC (논문 Algorithm 4)
    // 단순 nearest M개가 아니라, 다양한 방향으로 연결 분산
    // ─────────────────────────────────────────
    private List<Integer> selectNeighborsHeuristic(int baseId, List<int[]> candidates, int M) {
        List<Integer> result = new ArrayList<>();
        // 거리 오름차순 우선순위큐
        PriorityQueue<int[]> w = new PriorityQueue<>(
                Comparator.comparingDouble(a -> Float.intBitsToFloat(a[1]))
        );
        w.addAll(candidates);

        while (!w.isEmpty() && result.size() < M) {
            int[] e = w.poll();
            int eId = e[0];
            if (eId == baseId) continue;

            float distToBase = Float.intBitsToFloat(e[1]);

            // 핵심 휴리스틱: 이미 선택된 이웃들보다 base에 더 가까운 경우만 연결
            // → 클러스터 경계를 넘는 연결 확보 (논문 Fig.2 참고)
            boolean closer = true;
            for (int selectedId : result) {
                float distToSelected = distance(vectors.get(eId), vectors.get(selectedId));
                if (distToSelected < distToBase) {
                    closer = false;
                    break;
                }
            }
            if (closer) {
                result.add(eId);
            }
        }
        return result;
    }

    // ─────────────────────────────────────────
    // Pruning: 연결 수 초과 시 휴리스틱으로 축소
    // ─────────────────────────────────────────
    private void pruneConnections(int nodeId, int layer, int maxConn) {
        List<Integer> conns = graph.get(nodeId).get(layer);
        List<int[]> candidates = new ArrayList<>();
        for (int neighbor : conns) {
            float dist = distance(vectors.get(nodeId), vectors.get(neighbor));
            candidates.add(new int[]{neighbor, Float.floatToIntBits(dist)});
        }
        List<Integer> pruned = selectNeighborsHeuristic(nodeId, candidates, maxConn);
        conns.clear();
        conns.addAll(pruned);
    }

    // ─────────────────────────────────────────
    // K-NN-SEARCH (논문 Algorithm 5)
    // ─────────────────────────────────────────
    public List<SearchResult> search(float[] query, int K, int ef) {
        if (entryPoint == -1) return Collections.emptyList();

        int ep = entryPoint;

        // 상위 층에서 greedy로 진입점 좁히기
        for (int lc = maxLayer; lc >= 1; lc--) {
            List<int[]> w = searchLayer(query, ep, 1, lc);
            ep = w.get(0)[0];
        }

        // Layer 0에서 ef 크기로 정밀 검색
        List<int[]> w = searchLayer(query, ep, Math.max(ef, K), 0);

        // K개만 반환
        List<SearchResult> results = new ArrayList<>();
        for (int i = 0; i < Math.min(K, w.size()); i++) {
            int[] item = w.get(i);
            results.add(new SearchResult(item[0], Float.intBitsToFloat(item[1])));
        }
        return results;
    }

    // ─────────────────────────────────────────
    // 유틸
    // ─────────────────────────────────────────
    private List<Integer> getNeighbors(int nodeId, int layer) {
        List<List<Integer>> nodeLayers = graph.get(nodeId);
        if (layer >= nodeLayers.size()) return Collections.emptyList();
        return nodeLayers.get(layer);
    }

    // L2 거리 (유클리드)
    private float distance(float[] a, float[] b) {
        float sum = 0;
        for (int i = 0; i < a.length; i++) {
            float diff = a[i] - b[i];
            sum += diff * diff;
        }
        return (float) Math.sqrt(sum);
    }

    public record SearchResult(int id, float distance) {}

    // ─────────────────────────────────────────
    // 간단 테스트
    // ─────────────────────────────────────────
    public static void main(String[] args) {
        HNSWTest hnsw = new HNSWTest(16, 200);
        Random rand = new Random(0);
        int n = 10000;
        int dim = 128;

        System.out.println("=== HNSW 테스트 ===");
        System.out.printf("벡터 수: %d, 차원: %d%n%n", n, dim);

        // 데이터 삽입
        long insertStart = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            float[] v = new float[dim];
            for (int d = 0; d < dim; d++) v[d] = rand.nextFloat();
            hnsw.insert(v);
        }
        long insertTime = System.currentTimeMillis() - insertStart;
        System.out.printf("[인덱스 구축] %d ms%n", insertTime);

        // 쿼리 벡터
        float[] query = new float[dim];
        for (int d = 0; d < dim; d++) query[d] = rand.nextFloat();

        // HNSW 검색
        long hnswStart = System.currentTimeMillis();
        List<SearchResult> hnswResults = hnsw.search(query, 10, 50);
        long hnswTime = System.currentTimeMillis() - hnswStart;

        // 브루트포스 검색 (정답)
        long bfStart = System.currentTimeMillis();
        List<SearchResult> bfResults = bruteForce(hnsw.vectors, query, 10);
        long bfTime = System.currentTimeMillis() - bfStart;

        // 결과 비교
        System.out.printf("%n[HNSW 검색]      %d ms%n", hnswTime);
        System.out.printf("[브루트포스 검색] %d ms%n%n", bfTime);

        System.out.println("=== Top-10 결과 비교 ===");
        System.out.printf("%-5s %-20s %-20s%n", "순위", "HNSW (id/dist)", "BruteForce (id/dist)");
        System.out.println("-".repeat(50));
        for (int i = 0; i < 10; i++) {
            SearchResult h = hnswResults.get(i);
            SearchResult b = bfResults.get(i);
            System.out.printf("%-5d %-4d / %-13.6f %-4d / %-13.6f%n",
                    i + 1, h.id(), h.distance(), b.id(), b.distance());
        }

        // Recall 계산
        Set<Integer> hnswIds = new HashSet<>();
        Set<Integer> bfIds = new HashSet<>();
        for (SearchResult r : hnswResults) hnswIds.add(r.id());
        for (SearchResult r : bfResults) bfIds.add(r.id());
        hnswIds.retainAll(bfIds);
        System.out.printf("%nRecall@10: %.1f%%%n", hnswIds.size() * 10.0);
    }

    private static List<SearchResult> bruteForce(List<float[]> vectors, float[] query, int K) {
        List<SearchResult> all = new ArrayList<>();
        for (int i = 0; i < vectors.size(); i++) {
            float[] v = vectors.get(i);
            float sum = 0;
            for (int d = 0; d < query.length; d++) {
                float diff = query[d] - v[d];
                sum += diff * diff;
            }
            all.add(new SearchResult(i, (float) Math.sqrt(sum)));
        }
        all.sort(Comparator.comparingDouble(SearchResult::distance));
        return all.subList(0, K);
    }
}
