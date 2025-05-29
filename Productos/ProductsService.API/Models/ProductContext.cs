using Microsoft.EntityFrameworkCore;

namespace ProductsService.API.Models
{
    public class ProductContext : DbContext
    {
        public ProductContext(DbContextOptions<ProductContext> options)
            : base(options)
        {
        }

        public DbSet<Products> ProductItems { get; set; } = null!;
    }
}
