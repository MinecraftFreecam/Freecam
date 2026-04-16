import { z } from "zod";

export const MatrixUploadSchema = z
  .object({
    path: z.string(),
    name: z.string().optional(),
    days: z.number().int().min(1).max(90).default(90),
    archive: z.boolean().default(true),
  })
  .superRefine((val, ctx) => {
    if (val.archive) {
      if (val.name == null) {
        ctx.addIssue({
          code: "custom",
          message: "'name' is required when 'archive: true'",
        });
      }
    } else {
      if (val.name != null) {
        ctx.addIssue({
          code: "custom",
          message: "'name' not permitted when 'archive: false'",
        });
      }
    }
  });

export const MatrixJobSchema = z.object({
  name: z.string(),
  gradle_args: z.array(z.string()),
  upload: MatrixUploadSchema.optional(),
});

export const MatrixJobsFileSchema = z.array(MatrixJobSchema);

export type MatrixUpload = z.infer<typeof MatrixUploadSchema>;
export type MatrixJob = z.infer<typeof MatrixJobSchema>;
export type MatrixJobsFile = z.infer<typeof MatrixJobsFileSchema>;
