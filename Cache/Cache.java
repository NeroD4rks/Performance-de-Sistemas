public class Cache extends Memoria {
    private final int [][] dados;
    private final RAM ram;

    private final int length_row;

    public Cache(int W, int m, RAM ram) {     // W é a capacidade da memória em "words"
        super(W);
        this.dados = new int[m][W + 2];
        this.ram = ram;
        this.length_row = W;
    }

    public int[] get_endereco_in_cache(int endereco){
        System.out.println("READCACHE");
        int resp;
        System.out.println("x="+Integer.toBinaryString(endereco));
        int t, r, s, w;

        // extrair t, r e w

        // se cache line = 2^6 palavras, w possui 6 bits
        w = 0b11_1111 & endereco;
        System.out.println("w="+Integer.toBinaryString(w));

        // se cache possui 2^6 cache lines, r possui 6 bits (capacidade memoria principal)
        r = (endereco >> 6) & 0b11_1111;

        System.out.println("r="+r);

        // capacidade = 8M = 2^23, x é de 23 bits, t possui 11 bits
        // 12
        t = (endereco >> 6) & 0b111_1111_1111;
        System.out.println("t="+t);

        System.out.println("w=" + w);

        return new int[]{t, w, r};
    }
    public boolean tag_in_cache(int line, int tag){
        return this.dados[line][0] == tag;
    }
    public void change_ram(int tag, int line) throws EnderecoInvalido {
        int start = tag * length_row;
        for(int i=0; i < length_row; i++){
             ram.Write(start + i, dados[line][i+2]);
        }
    }
    public void collect_new_block(int tag, int line) throws EnderecoInvalido {
        System.out.println("Coletando novo bloco");
        int start = tag * length_row;

        if (dados[line][1] == 1) change_ram(tag, line);
        System.out.println(dados.length);
        for(int i=0; i < length_row; i++){
            dados[line][i+2] = ram.Read(start + i);
        }

        dados[line][0] = tag;
        dados[line][1] = 0; // status da modificação

        System.out.println("Terminado Coleta do bloco");
    }
    @Override
    public int Read(int endereco) throws EnderecoInvalido {
        int t=0, r=0, w=0;
        int[] analise = get_endereco_in_cache(endereco);

        t = analise[0];
        r = analise[1];
        w = analise[2] + 2;
        if(tag_in_cache(r, t)){
            System.out.println("Cache Hit!!!");
            return this.dados[r][w];
        }

        System.out.println("cache miss!!!");
        collect_new_block(t, r);
        return this.dados[r][w];

    }


    @Override
    public void Write(int endereco, int x) throws EnderecoInvalido {
        int t=0, r=0, w=0;
        int[] analise = get_endereco_in_cache(endereco);

        t = analise[0];
        r = analise[1];
        w = analise[2] + 2;

        if(tag_in_cache(r, t)){
            System.out.println("Cache Hit!!!");
            this.dados[r][w] = x;
        }else{
            System.out.println("cache miss!!!");
            collect_new_block(t, r);
            this.dados[r][w] = x;
        }



    }

}