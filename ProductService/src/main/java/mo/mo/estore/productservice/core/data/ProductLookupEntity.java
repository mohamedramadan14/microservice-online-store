package mo.mo.estore.productservice.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "productlookup")
public class ProductLookupEntity implements Serializable {
    @Serial
    private static  final long serialVersionUID = 2L;

    @Id
    private String productId;

    @Column(unique = true)
    private String title;
}
